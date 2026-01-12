package miwu.miot

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.Cookie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotBusinessException
import miwu.miot.exception.MiotHttpException
import miwu.miot.ktx.IO
import miwu.miot.model.MiotUser
import miwu.miot.model.login.Login
import miwu.miot.model.login.LoginQrCode
import miwu.miot.model.login.QrCodeSid
import miwu.miot.model.login.Sid
import miwu.miot.utils.cut
import miwu.miot.utils.getRandomDeviceId
import miwu.miot.utils.md5
import miwu.miot.utils.to
import miwu.miot.utils.urlEncode
import kotlin.coroutines.CoroutineContext
import kotlin.time.TimeSource

class MiotLoginClientImpl : MiotLoginClient {
    private val httpClient = HttpClient {
        createClientPlugin("UserAgent") {
            onRequest { request, _ ->
                request.headers.remove("User-Agent")
                request.headers.append("User-Agent", MI_HOME_USER_AGENT)
            }
        }
        install(HttpCookies) {
            storage = SimpleCookiesStorage()
        }
        expectSuccess = true
    }

    override suspend fun login(user: String, pwd: String): Result<MiotUser> = runCatching {
        val sidDetails = getSid()
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
        get<String>(Url.Login, body)
            .to<Login>()
            .login()
            .getOrThrow()
    }

    override suspend fun loginByQrCode(loginUrl: String) =
        get<String>(loginUrl).cut().to<Login>().run login@{
            getQrCodeSid().run {
                this@login.location = location
                this@login.securityToken = securityToken
                login()
            }
        }

    override suspend fun loginByQrCode(
        loginUrl: String,
        onSuccess: suspend CoroutineScope.(MiotUser) -> Unit,
        onTimeout: suspend CoroutineScope.() -> Unit,
        onFailure: suspend CoroutineScope.(Throwable?) -> Unit,
        context: CoroutineContext
    ): Unit = withContext(Dispatchers.IO) {
        try {
            get<String>(loginUrl).cut().to<Login>().apply {
                getSid().let { sid ->
                    location = sid.location
                    securityToken = sid.securityToken
                    login()
                        .onSuccess { user -> withContext(context) { onSuccess(user) } }
                        .onFailure { onFailure(it) }
                }
            }
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
            ${Url.QrCode}?
            ${
            """
                _qrsize=240
                qs=?sid=${MIOT_SID}
                callback=https://sts.api.io.mi.com/sts
                sid=${MIOT_SID}
                serviceParam=
                _locale=zh_CN
                _dc=${TimeSource.Monotonic.markNow()}
            """.trimIndent().urlEncode()
        }
        """.trimIndent()
        return@withContext get<String>(generateQrCode)
            .cut()
            .to<LoginQrCode>()
    }

    private object Url {
        const val Sid = "https://account.xiaomi.com/pass/serviceLogin?sid=$MIOT_SID&_json=true"
        const val Login = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        const val QrCode = "https://account.xiaomi.com/longPolling/loginUrl"
    }

    internal suspend inline fun <reified T> get(
        url: String, body: Any? = null
    ): T = httpClient.get(url) {
        setBody(body)
    }.body()

    private suspend fun getSid() = get<String>(Url.Sid).cut().to<Sid>()
    private suspend fun getQrCodeSid() = get<String>(Url.Sid).cut().to<QrCodeSid>()

    private suspend fun Login.login(): Result<MiotUser> = runCatching {
        if (code != 0) throw MiotBusinessException.loginFailed(code)
        val response = try {
            httpClient.get(location)
        } catch (e: Exception) {
            throw MiotHttpException("Login", e)
        }
        val cookiesHeader = response.headers["Set-Cookie"]!!
        val serviceToken = cookiesHeader.split(", ").firstNotNullOfOrNull { cookieString ->
            val parts = cookieString.split("; ")[0].split("=", limit = 2)
            if (parts.size == 2 && parts[0] == "serviceToken") {
                parts[1]
            } else {
                null
            }
        } ?: throw MiotAuthException.tokenMissing()
        MiotUser(userId.toString(), securityToken, serviceToken, getRandomDeviceId())
    }

    class SimpleCookiesStorage : CookiesStorage {
        private val storage = arrayListOf<Cookie>()

        override suspend fun get(requestUrl: io.ktor.http.Url): List<Cookie> = storage

        override suspend fun addCookie(requestUrl: io.ktor.http.Url, cookie: Cookie) {
            storage.add(cookie)
        }

        override fun close() = storage.clear()
    }
}