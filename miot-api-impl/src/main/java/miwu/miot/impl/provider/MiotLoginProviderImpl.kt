package miwu.miot.impl.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.common.MIOT_SID
import miwu.miot.common.MI_HOME_USER_AGENT
import miwu.miot.common.QRCODE_GENERATE_URL
import miwu.miot.common.SERVICE_LOGIN_AUTH_URL
import miwu.miot.common.SERVICE_LOGIN_URL
import miwu.miot.common.getRandomDeviceId
import miwu.miot.common.removePrefix
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotBusinessException
import miwu.miot.exception.MiotConnectionException
import miwu.miot.exception.MiotHttpException
import miwu.miot.exception.MiotTimeoutException
import miwu.miot.model.MiotUser
import miwu.miot.model.login.Login
import miwu.miot.model.login.LoginQrCode
import miwu.miot.model.login.ServiceData
import miwu.miot.model.login.Location
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.utils.FormBody
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.get
import miwu.miot.utils.md5
import miwu.miot.utils.to
import miwu.miot.utils.urlEncode
import miwu.miot.utils.userAgent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext

class MiotLoginProviderImpl : MiotLoginProvider {
    private val cookieJar = SimpleCookieJar()
    private val miotLoginClient = OkHttpClient {
        userAgent(MI_HOME_USER_AGENT)
        cookieJar(cookieJar)
        readTimeout(5, TimeUnit.MINUTES)
    }

    override suspend fun login(user: String, pwd: String): Result<MiotUser> = runCatching {
        cookieJar.clear()
        val sidDetails = getLocation().getOrThrow()
        val pwdHash = pwd.md5()
        val body = FormBody {
            add("qs", sidDetails.qs)
            add("sid", sidDetails.sid)
            add("_sign", sidDetails.sign)
            add("callback", sidDetails.callback)
            add("user", user)
            add("hash", pwdHash)
            add("_json", "true")
        }
        get<String>(SERVICE_LOGIN_AUTH_URL, body)
            .getOrThrow()
            .to<Login>()
            .getOrThrow()
            .execute()
            .getOrThrow()
    }

    override suspend fun loginByQrCode(loginUrl: String): Result<MiotUser> = runCatching {
        cookieJar.clear()
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
    ): Unit = withContext(Dispatchers.IO) {
        cookieJar.clear()
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
                .onSuccess { user -> withContext(context) { onSuccess(user) } }
                .onFailure { onFailure(it) }
        } catch (e: Exception) {
            withContext(context) {
                if (e is SocketTimeoutException || e is TimeoutException) {
                    onTimeout()
                } else {
                    onFailure(e)
                }
            }
        }
    }

    // 为啥这里要在IO上下文执行？
    override suspend fun generateLoginQrCode(): Result<LoginQrCode> = runCatching {
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
                _dc=${System.currentTimeMillis()}
            """.trimIndent().urlEncode()
        }
        """.trimIndent()
        get<String>(generateQrCode)
            .getOrThrow()
            .removePrefix()
            .to<LoginQrCode>()
            .getOrThrow()
    }

    override suspend fun refreshServiceToken(miotUser: MiotUser): Result<MiotUser> = runCatching {
        cookieJar.clear()
        // 因为SimpleCookieJar的Store和Url无关，如果是手动添加Cookie是否不需要添加Url了呢
        // 所以添加一个手动实现addAll函数表示是手动添加Cookie
        val cookies = with(miotUser) {
            listOf(
                Cookie("userId", userId),
                Cookie("cUserId", cUserId),
                Cookie("nonce", nonce.toString()),
                Cookie("ssecurity", ssecurity),
                Cookie("psecurity", passToken),
                Cookie("passToken", passToken),
            )
        }
        cookieJar.addAll(cookies)
        val data = getLocation().getOrThrow()
        val location = data.location
        val serviceToken = getServiceToken(location).getOrThrow()
        miotUser.copy(
            ssecurity = data.ssecurity,
            serviceToken = serviceToken,
        )
    }

    private fun Cookie(name: String, value: String): Cookie =
        Cookie.Builder()
            .name(name)
            .value(value)
            .build()

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

    private suspend fun getServiceToken(location: String): Result<String> = runCatching {
        val response = get<Response>(location).getOrElse { e ->
            when (e) {
                is TimeoutException -> throw MiotTimeoutException("Login", e)
                is IOException -> throw MiotConnectionException("Login", e)
                else -> throw MiotHttpException("Login", e)
            }
        }
        val cookiesHeader = response.headers["Set-Cookie"]!!
        cookiesHeader.split(", ").firstNotNullOfOrNull { cookieString ->
            val parts = cookieString.split("; ")[0].split("=", limit = 2)
            if (parts.size == 2 && parts[0] == "serviceToken") {
                parts[1]
            } else {
                null
            }
        } ?: throw MiotAuthException.tokenMissing()
    }

    private suspend fun getLocation(): Result<Location> = runCatching {
        // 如果Location的code不是预期的是否可以在这里就把结果包装成异常
        get<String>(SERVICE_LOGIN_URL)
            .getOrThrow()
            .removePrefix()
            .to<Location>()
            .getOrThrow()
            .getOrThrowAuthException()
    }

    private suspend fun getServiceData(): Result<ServiceData> = runCatching {
        get<String>(SERVICE_LOGIN_URL)
            .getOrThrow()
            .removePrefix()
            .to<ServiceData>()
            .getOrThrow()
    }

    private suspend inline fun <reified T> get(url: String, body: RequestBody? = null): Result<T> =
        miotLoginClient.get<T>(url, body)

    class SimpleCookieJar : CookieJar {
        private val storage = arrayListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            storage.addAll(cookies)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> = storage

        fun addAll(cookies: List<Cookie>) = storage.addAll(cookies)

        fun clear() = storage.clear()
    }
}