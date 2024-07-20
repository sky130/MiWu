package miot.kotlin

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.helper.convertLanguage
import miot.kotlin.helper.getLanguageMap
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.login.Login
import miot.kotlin.model.login.LoginQrCode
import miot.kotlin.model.login.Sid
import miot.kotlin.service.SpecService
import miot.kotlin.utils.create
import miot.kotlin.utils.urlEncode
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import java.util.*

object MiotManager {
    private val loginClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request: Request = chain.request().newBuilder().removeHeader("User-Agent") //移除旧的
            .addHeader(
                "User-Agent",
                USER_AGENT,
            ).build()
        chain.proceed(request)
    }.cookieJar(SimpleCookieJar()).readTimeout(5, TimeUnit.MINUTES).build()
    private val retrofit = Retrofit.Builder().baseUrl(SPEC_SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create()).build()
    private val specService = retrofit.create<SpecService>()
    private val RANDOM_TEMP_CHARS = "0123456789ABCDEF".toCharArray()
    private val random = Random.Default
    internal val gson = Gson()
    internal var base64Encode : (ByteArray) -> String = {
        Base64.getEncoder().encodeToString(it)
    }
    internal var base64Decode : (String) -> ByteArray = {
        Base64.getDecoder().decode(signedNonce)
    }

    fun configBase64(encode:(ByteArray) -> String, decode:(String) -> ByteArray){
        base64Encode = encode
        base64Decode = decode
    }

    fun get(url: String, body: RequestBody? = null) = loginClient.run {
        newCall(
            Request.Builder().url(url).apply {
                if (body != null) post(body)
            }.build()
        ).execute().body.string()
    }

    fun getResponse(url: String, body: RequestBody? = null) = loginClient.run {
        newCall(
            Request.Builder().url(url).apply {
                if (body != null) post(body)
            }.build()
        ).execute()
    }

    private fun getSid() = gson.fromJson(
        get("https://account.xiaomi.com/pass/serviceLogin?sid=$MIOT_SID&_json=true").substring(
            11
        ), Sid::class.java
    )

    private inline fun <reified T> Gson.fromJson(str: String) =
        this.fromJson<T>(str, object : TypeToken<T>() {}.type)

    fun getRandomDeviceId(): String =
        (1..16).map { _ -> RANDOM_TEMP_CHARS.random() }.joinToString("")

    suspend fun login(user: String, pwd: String): Miot.MiotUser? = withContext(Dispatchers.IO) {
        val sidMsg = getSid()
        val url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        val pwdHash = MessageDigest.getInstance("MD5").digest(pwd.toByteArray())
            .joinToString("") { "%02x".format(it) }.uppercase(Locale.ROOT).padEnd(32, '0')
        val body = FormBody.Builder().apply {
            sidMsg.apply {
                add("qs", this.qs)
                add("sid", this.sid)
                add("_sign", this.sign)
                add("callback", this.callback)
            }
            add("user", user)
            add("hash", pwdHash)
            add("_json", "true")
        }.build()
        val result = get(url, body).substring(11)
        return@withContext gson.fromJson<Login>(result).login()
    }

    suspend fun loginByQrCode(
        loginUrl: String,
        onSuccess: suspend CoroutineScope.(Miot.MiotUser) -> Unit,
        onTimeout: suspend CoroutineScope.() -> Unit,
        onFailure: suspend CoroutineScope.(Throwable?) -> Unit,
        context:CoroutineContext = Dispatchers.Main
    ): Unit = withContext(Dispatchers.IO) {
        try {
            gson.fromJson<Login>(get(loginUrl).substring(11)).apply {
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

    private fun Login.login(): Miot.MiotUser? {
        if (code == 0) {
            getResponse(location).apply {
                for (i in (headers["Set-Cookie"] ?: return null).split(", ")) {
                    val parts = i.split("; ")[0].split("=", limit = 2)
                    if (parts[0] != "serviceToken") continue
                    if (parts.size == 2) {
                        val serviceToken = parts[1]
                        return Miot.MiotUser(userId, securityToken, serviceToken)
                    }
                }
            }
        }
        return null
    }

    suspend fun getSpecAtt(urn: String) = withContext(Dispatchers.IO) {
        specService.getInstance(urn).execute().body()
    }

    suspend fun getSpecMultiLanguage(urn: String) = withContext(Dispatchers.IO) {
        specService.getSpecMultiLanguage(urn).execute().body()?.string()
    }

    suspend fun getSpecAttWithLanguage(urn: String, languageCode: String = "zh_cn"): SpecAtt? {
        val att = getSpecAtt(urn) ?: return null
        val language = getSpecMultiLanguage(urn) ?: return att
        val map = getLanguageMap(language, languageCode) ?: return att
        return att.convertLanguage(map)
    }

    suspend fun getLoginQrCode() = withContext(Dispatchers.IO) {
        val generateQrCode = """
            https://account.xiaomi.com/longPolling/loginUrl?
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
        try {
            return@withContext gson.fromJson(
                get(generateQrCode).substring(11), LoginQrCode::class.java
            ).toQrCode()
        } catch (e: Exception) {
            return@withContext null
        }
    }


    fun from(user: Miot.MiotUser) = Miot(user)

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
