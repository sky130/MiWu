package miwu.miot.kmp.impl.provider

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.fillDefaults
import io.ktor.client.plugins.cookies.matches
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpHeaders
import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import miwu.miot.common.MIOT_SID
import miwu.miot.common.QRCODE_GENERATE_URL
import miwu.miot.common.SERVICE_LOGIN_AUTH_URL
import miwu.miot.common.SERVICE_LOGIN_URL
import miwu.miot.common.getRandomDeviceId
import miwu.miot.common.removePrefix
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotBusinessException
import miwu.miot.exception.MiotHttpException
import miwu.dispatchers.IoDispatcher
import miwu.miot.kmp.utils.MiotHttpClient
import miwu.miot.kmp.utils.json
import miwu.miot.kmp.utils.md5
import miwu.miot.kmp.utils.to
import miwu.miot.model.MiotUser
import miwu.miot.model.login.Location
import miwu.miot.model.login.Login
import miwu.miot.model.login.LoginQrCode
import miwu.miot.model.login.ServiceData
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.utils.runCatchingSuspend
import org.koin.core.annotation.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

@Singleton
class MiotLoginProviderImpl(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MiotLoginProvider {
    private val cookiesStorage = SimpleCookiesStorage()
    private val httpClient = MiotHttpClient {
        install(HttpCookies) {
            storage = cookiesStorage
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
        install(HttpTimeout) {
            val millis = 30 * 1000L
            socketTimeoutMillis = millis
            requestTimeoutMillis = millis
            connectTimeoutMillis = millis
        }
        expectSuccess = true
    }

    override suspend fun login(user: String, pwd: String): Result<MiotUser> = runCatchingSuspend {
        cookiesStorage.clear()
        val sidDetails = getLocation().getOrThrow()
        val pwdHash = pwd.md5()
        val body = parameters {
            append("qs", sidDetails.qs ?: "")
            append("sid", sidDetails.sid ?: "")
            append("_sign", sidDetails.sign ?: "")
            append("callback", sidDetails.callback ?: "")
            append("user", user)
            append("hash", pwdHash)
            append("_json", "true")
        }
        get<String>(SERVICE_LOGIN_AUTH_URL, body)
            .getOrThrow()
            .to<Login>()
            .getOrThrow()
            .execute()
            .getOrThrow()
    }

    override suspend fun loginByQrCode(loginUrl: String): Result<MiotUser> = runCatchingSuspend {
        cookiesStorage.clear()
        get<String>(loginUrl)
            .getOrThrow()
            .removePrefix()
            .to<Login>()
            .getOrThrow()
            .also {
                val (location, securityToken) = getServiceData().getOrThrow()
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
    ): Unit = withContext(ioDispatcher) {
        cookiesStorage.clear()
        try {
            get<String>(loginUrl)
                .getOrThrow()
                .removePrefix()
                .to<Login>()
                .getOrThrow()
                .also {
                    val (location, securityToken) = getServiceData().getOrThrow()
                    it.location = location
                    it.ssecurity = securityToken
                }
                .execute()
                .fold(
                    onSuccess = { user -> withContext(context) { onSuccess(user) } },
                    onFailure = { throw it },
                )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            withContext(context) {
                if (e is SocketTimeoutException) {
                    onTimeout()
                } else {
                    onFailure(e)
                }
            }
        }
    }

    override suspend fun generateLoginQrCode() = runCatchingSuspend {
        val generateQrCode = buildLoginQrCodeUrl(Clock.System.now().toEpochMilliseconds())
        get<String>(generateQrCode)
            .getOrThrow()
            .removePrefix()
            .to<LoginQrCode>()
            .getOrThrow()
    }

    override suspend fun refreshServiceToken(miotUser: MiotUser) = runCatchingSuspend {
        cookiesStorage.clear()
        val cookies = with(miotUser) {
            listOf(
                Cookie("deviceId", deviceId, encoding = CookieEncoding.RAW),
                Cookie("userId", userId, encoding = CookieEncoding.RAW),
                Cookie("cUserId", cUserId, encoding = CookieEncoding.RAW),
                Cookie("passToken", passToken, encoding = CookieEncoding.RAW),
            )
        }
        cookiesStorage.addAll(Url(SERVICE_LOGIN_URL), cookies)
        val data = getLocation().getOrThrow()
        val location = data.location
        val serviceToken = getServiceToken(location).getOrThrow()
        miotUser.copy(
            ssecurity = data.ssecurity,
            serviceToken = serviceToken,
        )
    }

    private suspend fun Login.execute(): Result<MiotUser> = runCatchingSuspend {
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

    private suspend fun getServiceToken(location: String) = runCatchingSuspend {
        val response = try {
            httpClient.get(location)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            throw MiotHttpException("Login", e)
        }
        response.headers.findSetCookie("serviceToken")
            ?.value
            ?: throw MiotAuthException.tokenMissing()
    }

    private suspend fun getLocation(): Result<Location> = runCatchingSuspend {
        get<String>(SERVICE_LOGIN_URL)
            .getOrThrow()
            .removePrefix()
            .to<Location>()
            .getOrThrow()
            .getOrThrowAuthException()
    }

    private suspend fun getServiceData(): Result<ServiceData> = runCatchingSuspend {
        get<String>(SERVICE_LOGIN_URL)
            .getOrThrow()
            .removePrefix()
            .to<ServiceData>()
            .getOrThrow()
    }

    private suspend inline fun <reified T> get(
        url: String,
        body: Any? = null,
    ): Result<T> = runCatchingSuspend {
        if (body is Parameters) {
            httpClient.post(url) { setBody(FormDataContent(body)) }.body<T>()
        } else {
            httpClient.get(url) { if (body != null) setBody(body) }.body<T>()
        }
    }

    private class SimpleCookiesStorage : CookiesStorage {
        private data class StoredCookie(val cookie: Cookie, val createdAtMillis: Long)

        private val storage = mutableListOf<StoredCookie>()
        private val mutex = Mutex()

        override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
            removeExpired()
            storage.map(StoredCookie::cookie).filter { it.matches(requestUrl) }
        }

        override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
            mutex.withLock {
                val normalized = cookie.fillDefaults(requestUrl)
                storage.removeAll {
                    it.cookie.name == normalized.name &&
                        it.cookie.domain == normalized.domain &&
                        it.cookie.path == normalized.path
                }
                storage += StoredCookie(normalized, Clock.System.now().toEpochMilliseconds())
                removeExpired()
            }
        }

        override fun close() {
            // The owning HttpClient closes this storage after requests have stopped.
        }

        suspend fun addAll(requestUrl: Url, cookies: List<Cookie>) {
            cookies.forEach { addCookie(requestUrl, it) }
        }

        suspend fun clear() = mutex.withLock { storage.clear() }

        private fun removeExpired() {
            val now = Clock.System.now().toEpochMilliseconds()
            storage.removeAll { stored ->
                val expiresAt = stored.cookie.maxAge?.let { stored.createdAtMillis + it * 1_000L }
                    ?: stored.cookie.expires?.timestamp
                expiresAt != null && expiresAt <= now
            }
        }
    }

}

internal fun Headers.findSetCookie(name: String): Cookie? =
    getAll(HttpHeaders.SetCookie)
        ?.flatMap { it.splitSetCookieHeader() }
        ?.map { parseServerSetCookieHeader(it) }
        ?.firstOrNull { it.name == name }

internal fun String.splitSetCookieHeader(): List<String> {
    var comma = indexOf(',')

    if (comma == -1) {
        return listOf(this)
    }

    val result = mutableListOf<String>()
    var current = 0

    var equals = indexOf('=', comma)
    var semicolon = indexOf(';', comma)
    while (current < length && comma > 0) {
        if (equals < comma) {
            equals = indexOf('=', comma)
        }

        var nextComma = indexOf(',', comma + 1)
        while (nextComma in 0..<equals) {
            comma = nextComma
            nextComma = indexOf(',', nextComma + 1)
        }

        if (semicolon < comma) {
            semicolon = indexOf(';', comma)
        }

        // No more keys remaining.
        if (equals < 0) {
            result += substring(current)
            return result
        }

        // No ';' between ',' and '=' => We're on a header border.
        if (semicolon == -1 || semicolon > equals) {
            result += substring(current, comma)
            current = comma + 1
            // Update comma index at the end of loop.
        }

        // ',' in value, skip it and find next.
        comma = nextComma
    }

    if (current < length) {
        result += substring(current)
    }

    return result
}

internal fun buildLoginQrCodeUrl(nowMillis: Long): String =
    URLBuilder(QRCODE_GENERATE_URL).apply {
        parameters.append("_qrsize", "240")
        parameters.append("qs", "?sid=$MIOT_SID")
        parameters.append("callback", "https://sts.api.io.mi.com/sts")
        parameters.append("sid", MIOT_SID)
        parameters.append("serviceParam", "")
        parameters.append("_locale", "zh_CN")
        parameters.append("_dc", nowMillis.toString())
    }.buildString()
