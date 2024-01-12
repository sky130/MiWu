package miot.kotlin

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.helper.convertLanguage
import miot.kotlin.helper.getLanguageMap
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.login.Login
import miot.kotlin.model.login.Sid
import miot.kotlin.service.SpecService
import miot.kotlin.utils.create
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

object MiotManager {
    private val loginClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request().newBuilder().removeHeader("User-Agent") //移除旧的
                .addHeader(
                    "User-Agent",
                    USER_AGENT,
                ).build()
            chain.proceed(request)
        }.build()
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SPEC_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
    private val specService by lazy { retrofit.create<SpecService>() }
    private val RANDOM_TEMP_CHARS = "0123456789ABCDEF".toCharArray()
    private val random = Random.Default
    internal val gson by lazy { Gson() }

    internal fun get(url: String, body: RequestBody? = null) = loginClient.run {
        newCall(
            Request.Builder().url(url).apply {
                if (body != null) post(body)
            }.build()
        ).execute().body.string()
    }

    internal fun getResponse(url: String, body: RequestBody? = null) = loginClient.run {
        newCall(
            Request.Builder().url(url).apply {
                if (body != null) post(body)
            }.build()
        ).execute()
    }

    private fun getSid() =
        gson.fromJson(
            get("https://account.xiaomi.com/pass/serviceLogin?sid=$MI_SID&_json=true").substring(11),
            Sid::class.java
        )

    private inline fun <reified T> Gson.fromJson(str: String) = this.fromJson<T>(str, object : TypeToken<T>() {}.type)

    fun getRandomDeviceId(): String = (1..16).map { _ -> RANDOM_TEMP_CHARS.random() }.joinToString("")

    suspend fun login(user: String, pwd: String): Miot.MiotUser? = withContext(Dispatchers.IO) {
        val sidMsg = getSid()
        val url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        val pwdHash =
            MessageDigest.getInstance("MD5").digest(pwd.toByteArray()).joinToString("") { "%02x".format(it) }
                .uppercase(Locale.ROOT).padEnd(32, '0')
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
        gson.fromJson<Login>(result).apply {
            if (code == 0) {
                // 登录成功
                getResponse(location).apply {
                    for (i in (headers["Set-Cookie"] ?: return@withContext null).split(", ")) {
                        val parts = i.split("; ")[0].split("=", limit = 2)
                        if (parts.size == 2) {
                            val serviceToken = parts[1]
                            return@withContext Miot.MiotUser(userId, securityToken, serviceToken)
                        }
                    }
                }
            }
        }
        return@withContext null
    }

    suspend fun getSpecAtt(urn: String) = withContext(Dispatchers.IO) {
        specService.getInstance(urn).execute().body()
    }

    suspend fun getSpecMultiLanguage(urn: String) = withContext(Dispatchers.IO) {
        specService.getSpecMultiLanguage(urn).execute().body()?.string()
    }

    suspend fun getSpecAttWithLanguage(urn: String, languageCode: String="zh_cn"): SpecAtt? {
        val att = getSpecAtt(urn) ?: return null
        val language = getSpecMultiLanguage(urn) ?: return att
        val map = getLanguageMap(language, languageCode) ?: return att
        return att.convertLanguage(map)
    }

    fun from(user: Miot.MiotUser) = Miot(user)
}