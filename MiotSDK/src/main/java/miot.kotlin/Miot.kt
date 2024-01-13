package miot.kotlin

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.exception.MiotUnauthorizedException
import miot.kotlin.helper.Action
import miot.kotlin.helper.GetAtt
import miot.kotlin.helper.SetAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes
import miot.kotlin.service.MiotService
import miot.kotlin.service.body.*
import miot.kotlin.utils.create
import miot.kotlin.utils.generateSignature
import miot.kotlin.utils.generateSignedNonce
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.nio.charset.Charset


class Miot(private val user: MiotUser) {
    private val miotClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request
            user.apply {
                if (serviceToken.isEmpty() || securityToken.isEmpty()) {
                    throw MiotUnauthorizedException("serviceToken or securityToken not found.")
                }
                request = chain.request().newBuilder().apply {
                    val data =
                        getBodyStr(chain.request().body ?: throw RuntimeException("body is empty"))
                    val nonce = getNonce()
                    val signedNonce = generateSignedNonce(securityToken, nonce)
                    val signature = generateSignature(
                        chain.request().url.toString().replace(MIOT_SERVER_URL, "/"),
                        signedNonce,
                        nonce,
                        data
                    )
                    removeHeader("User-Agent")
                    addHeader("User-Agent", USER_AGENT)
                    addHeader("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
                    addHeader(
                        "Cookie",
                        "PassportDeviceId=${deviceId};userId=${userId};serviceToken=$serviceToken;"
                    )
                    post(
                        FormBody.Builder().add("_nonce", nonce).add("data", data)
                            .add("signature", signature).build()
                    )
                }.build()
            }
            chain.proceed(request)
        }.build()
    }
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(MIOT_SERVER_URL).client(miotClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    private val tempChars =
        "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    private fun getNonce() = (1..16).map { _ -> tempChars.random() }.joinToString("")
    private fun getBodyStr(body: RequestBody): String {
        Buffer().apply {
            body.writeTo(this)
            val contentType = body.contentType()
            if (contentType != null) {
                return readString(
                    contentType.charset(
                        Charset.forName("UTF-8")
                    )!!
                )
            }
        }
        throw RuntimeException("data of requestBody is empty")
    }

    private val miotService by lazy {
        retrofit.create<MiotService>()
    }

    data class MiotUser(
        val userId: String,
        val securityToken: String,
        val serviceToken: String,
        val deviceId: String = MiotManager.getRandomDeviceId(),
    )


    suspend fun getHomes(
        appVer: Int = 7,
        fetchShare: Boolean = true,
        fetchShareDev: Boolean = true,
        limit: Int = 300,
    ) = withContext(Dispatchers.IO) {
        return@withContext miotService.getHomes(
            GetHome(
                appVer, fetchShare, fetchShareDev, false, limit
            )
        ).execute().body()
    }

    suspend fun getDevices(
        homeOwnerId: Long, homeId: Long, limit: Int = 200,
    ) = withContext(Dispatchers.IO) {
        return@withContext miotService.getDevices(
            GetDevices(homeOwnerId, homeId, limit)
        ).execute().body()
    }

    suspend fun getScenes(
        homeId: Long,
    ) = withContext(Dispatchers.IO) {
        return@withContext miotService.getScenes(GetScene(homeId)).execute().body()
    }

    /**
     * @return 目前不考虑返回结果
     */
    suspend fun runScene(scene: MiotScenes.Result.Scene) = withContext(Dispatchers.IO) {
        return@withContext if (scene.icon.isEmpty()) {
            miotService.runScene(RunScene(scene.sceneId.toLong()))
        } else {
            miotService.runScene(RunCommonScene(scene.sceneId.toLong()))
        }.execute().body()
    }

    suspend fun getDeviceAtt(device: MiotDevices.Result.Device, att: Array<out GetAtt>) =
        withContext(Dispatchers.IO) {
            val list = Array(att.size) {
                att[it].run {
                    GetParams.Att(device.did, siid, piid)
                }
            }
            return@withContext miotService.getDeviceAtt(GetParams(list)).execute().body()
        }

    suspend fun setDeviceAtt(device: MiotDevices.Result.Device, att: Array<out SetAtt>) =
        withContext(Dispatchers.IO) {
            val list = Array(att.size) {
                att[it].run {
                    SetParams.Att(device.did, siid, piid, value)
                }
            }
            return@withContext miotService.setDeviceAtt(SetParams(list)).execute().body()
        }

    suspend fun doAction(device: MiotDevices.Result.Device, siid: Int, aiid: Int) =
        withContext(Dispatchers.IO) {
            return@withContext miotService.doAction(
                ActionBody(
                    ActionBody.Action(
                        device.did, siid, aiid
                    )
                )
            ).execute().body()
        }

    suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        return@withContext miotService.getUserInfo(GetUserInfo(user.userId)).execute().body()
    }
}