package miwu.miot

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.model.login.Login
import miwu.miot.model.login.LoginQrCode
import miwu.miot.model.login.Sid
import miwu.miot.ktx.FormBody
import miwu.miot.ktx.OkHttpClient
import miwu.miot.ktx.get
import miwu.miot.ktx.userAgent
import miwu.miot.model.MiotUser
import miwu.miot.utils.cut
import miwu.miot.utils.getRandomDeviceId
import miwu.miot.utils.md5
import miwu.miot.utils.to
import miwu.miot.utils.urlEncode
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

class MiotLoginClientImpl : MiotLoginClient {
    private val miotLoginClient = OkHttpClient {
        userAgent(MI_HOME_USER_AGENT)
        cookieJar(SimpleCookieJar())
        readTimeout(5, TimeUnit.MINUTES)
    }

    override suspend fun login(user: String, pwd: String): MiotUser? {
        return try {
            val sidDetails = getSid()
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
            return get<String>(Url.Login, body).to<Login>().login()
        } catch (e: IOException) {
            null
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loginByQrCode(loginUrl: String) =
        get<String>(loginUrl).cut().to<Login>().run login@{
            getSid().run {
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
                getSid().let {
                    location = it.location
                    securityToken = it.securityToken
                    login().let { user ->
                        withContext(context) {
                            if (user == null) {
                                onFailure(null)
                            } else {
                                onSuccess(user)
                            }
                        }
                    }
                }
            }
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
                _dc=${System.currentTimeMillis()}
            """.trimIndent().urlEncode()
        }
        """.trimIndent()
        return@withContext get<String>(generateQrCode)
            .cut()
            .to<LoginQrCode>()
    }

    private object Url {
        val Sid = "https://account.xiaomi.com/pass/serviceLogin?sid=$MIOT_SID&_json=true"
        val Login = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        val QrCode = "https://account.xiaomi.com/longPolling/loginUrl"
    }

    internal suspend inline fun <reified T> get(
        url: String, body: RequestBody? = null
    ): T = miotLoginClient.get<T>(url, body)

    private suspend fun getSid() = get<String>(Url.Sid).cut().to<Sid>()

    private suspend fun Login.login(): MiotUser? {
        if (code != 0) return null
        val response = try {
            get<Response>(location)
        } catch (e: Exception) {
            return null
        }
        val cookiesHeader = response.headers["Set-Cookie"] ?: return null
        val serviceToken = cookiesHeader.split(", ").firstNotNullOfOrNull { cookieString ->
            val parts = cookieString.split("; ")[0].split("=", limit = 2)
            if (parts.size == 2 && parts[0] == "serviceToken") {
                parts[1]
            } else {
                null
            }
        }
        return serviceToken?.let { MiotUser(userId, securityToken, it, getRandomDeviceId()) }
    }

    class SimpleCookieJar : CookieJar {
        private val storage = arrayListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            storage.addAll(cookies)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return storage
        }
    }
}