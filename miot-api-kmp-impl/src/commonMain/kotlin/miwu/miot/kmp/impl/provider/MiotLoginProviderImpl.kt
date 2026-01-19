package miwu.miot.kmp.impl.provider

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.kmp.utils.IO
import miwu.miot.kmp.plugin.SupportInvalidExpiresHttpCookies
import miwu.miot.kmp.plugin.parseServerSetCookieHeader
import miwu.miot.kmp.plugin.splitSetCookieHeader
import miwu.miot.kmp.utils.json
import miwu.miot.kmp.utils.md5
import miwu.miot.kmp.utils.to
import miwu.miot.common.MIOT_SID
import miwu.miot.common.QRCODE_GENERATE_URL
import miwu.miot.common.SERVICE_LOGIN_AUTH_URL
import miwu.miot.common.SERVICE_LOGIN_URL
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotBusinessException
import miwu.miot.exception.MiotHttpException
import miwu.miot.model.MiotUser
import miwu.miot.model.login.Login
import miwu.miot.model.login.LoginQrCode
import miwu.miot.provider.MiotLoginProvider
import kotlin.coroutines.CoroutineContext
import miwu.miot.common.getRandomDeviceId
import miwu.miot.common.removePrefix
import miwu.miot.model.login.ServiceData
import miwu.miot.model.login.Location
import kotlin.time.Clock

class MiotLoginProviderImpl : MiotLoginProvider {
    private val cookiesStorage = SimpleCookiesStorage()
    private val httpClient = HttpClient {
        install(SupportInvalidExpiresHttpCookies) {
            storage = cookiesStorage
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20 * 1000L
        }
        expectSuccess = true
    }

    override suspend fun login(
        user: String,
        pwd: String
    ) = runCatching {
        cookiesStorage.close()
        val sidDetails = getLocation()
        val pwdHash = pwd.md5()
        val body = formData {
            append("qs", sidDetails.qs)
            append("sid", sidDetails.sid)
            append("_sign", sidDetails.sign)
            append("callback", sidDetails.callback)
            append("user", user)
            append("hash", pwdHash)
            append("_json", "true")
        }
        get<String>(SERVICE_LOGIN_AUTH_URL, body)
            .to<Login>()
            .execute()
            .getOrThrow()
    }

    override suspend fun loginByQrCode(loginUrl: String) = runCatching {
        cookiesStorage.close()
        get<String>(loginUrl)
            .removePrefix()
            .to<Login>()
            .also {
                val (location, securityToken) = getServiceData()
                it.location = location
                it.ssecurity = securityToken
            }
            .execute()
            .getOrThrow()
    }

    override suspend fun loginByQrCode(
        loginUrl: String,
        onSuccess: suspend CoroutineScope.(MiotUser) -> Unit,
        onTimeout: suspend CoroutineScope.() -> Unit,
        onFailure: suspend CoroutineScope.(Throwable?) -> Unit,
        context: CoroutineContext
    ): Unit = withContext(Dispatchers.IO) {
        cookiesStorage.close()
        try {
            get<String>(loginUrl)
                .removePrefix()
                .to<Login>()
                .also {
                    val (location, securityToken) = getServiceData()
                    it.location = location
                    it.ssecurity = securityToken
                }
                .execute()
                .onSuccess { user -> withContext(context) { onSuccess(user) } }
                .onFailure { onFailure(it) }
        } catch (e: Exception) {
            withContext(context) {
                if (e is SocketTimeoutException) {
                    onTimeout()
                } else {
                    onFailure(e)
                }
            }
        }
    }

    override suspend fun generateLoginQrCode() = withContext(Dispatchers.IO) {
        val generateQrCode = """
            ${QRCODE_GENERATE_URL}?
            ${
            """
                _qrsize=240
                qs=?sid=${MIOT_SID}
                callback=https://sts.api.io.mi.com/sts
                sid=${MIOT_SID}
                serviceParam=
                _locale=zh_CN
                _dc=${Clock.System.now().toEpochMilliseconds()}
            """.trimIndent().parseUrlEncodedParameters()
        }
        """.trimIndent()
        runCatching {
            get<String>(generateQrCode)
                .removePrefix()
                .to<LoginQrCode>()
        }
    }

    override suspend fun refreshServiceToken(miotUser: MiotUser) = runCatching {
        cookiesStorage.close()
        with(miotUser) {
            listOf(
                Cookie("userId", userId),
                Cookie("cUserId", cUserId),
                Cookie("nonce", nonce),
                Cookie("ssecurity", ssecurity),
                Cookie("psecurity", passToken),
                Cookie("passToken", passToken),
            )
        }.forEach { cookiesStorage.addCookie(Url(""), it) }
        val data = getLocation()
        val location = data.location
        val serviceToken = getServiceToken(location).getOrThrow()
        miotUser.copy(
            ssecurity = data.ssecurity,
            serviceToken = serviceToken
        )
    }

    private suspend fun Login.execute(): Result<MiotUser> = runCatching {
        if (code != 0) throw MiotBusinessException.loginFailed(code)
        val serviceToken = getServiceToken(location).getOrThrow()
        MiotUser(
            userId.toString(),
            cUserId,
            nonce,
            ssecurity,
            psecurity,
            passToken,
            serviceToken,
            getRandomDeviceId()
        )
    }

    private suspend fun getServiceToken(location: String) = runCatching {
        val response = try {
            httpClient.get(location)
        } catch (e: Exception) {
            throw MiotHttpException("Login", e)
        }
        response.headers.getAll(HttpHeaders.SetCookie)
            ?.flatMap { it.splitSetCookieHeader() }
            ?.map { parseServerSetCookieHeader(it) }
            ?.first { it.name == "serviceToken" }
            ?.value
            ?: throw MiotAuthException.tokenMissing()
    }

    private suspend fun getLocation() =
        get<String>(SERVICE_LOGIN_URL)
            .removePrefix()
            .to<Location>()

    private suspend fun getServiceData() =
        get<String>(SERVICE_LOGIN_URL)
            .removePrefix()
            .to<ServiceData>()

    private suspend inline fun <reified T> get(
        url: String, body: Any? = null
    ): T = httpClient.get(url) {
        setBody(body)
    }.body()

    private class SimpleCookiesStorage : CookiesStorage {
        private val storage = mutableListOf<Cookie>()

        override suspend fun get(requestUrl: Url): List<Cookie> {
            return storage
        }

        override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
            storage.add(cookie)
        }

        override fun close() {
            storage.clear()
        }
    }
}