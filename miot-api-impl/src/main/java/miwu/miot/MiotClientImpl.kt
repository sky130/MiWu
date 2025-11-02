package miwu.miot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.att.get.GetAtt
import miwu.miot.att.get.piid
import miwu.miot.att.get.siid
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotScenes
import miwu.miot.service.MiotService
import miwu.miot.service.body.ActionBody
import miwu.miot.service.body.GetDevices
import miwu.miot.service.body.GetHome
import miwu.miot.service.body.GetParams
import miwu.miot.service.body.GetScene
import miwu.miot.service.body.GetUserInfo
import miwu.miot.service.body.RunCommonScene
import miwu.miot.service.body.RunScene
import miwu.miot.service.body.SetParams
import miwu.miot.ktx.FormBody
import miwu.miot.ktx.OkHttpClient
import miwu.miot.ktx.Retrofit
import miwu.miot.ktx.addHeaders
import miwu.miot.ktx.create
import miwu.miot.ktx.get
import miwu.miot.ktx.readToString
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevices.Result.Info
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.utils.getNonce
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class MiotClientImpl : MiotClient {
    private val miotClient by lazy {
        OkHttpClient {
            addInterceptor(MiotAuthInterceptor(user))
        }
    }
    private val iconClient by lazy {
        OkHttpClient { }
    }
    private val miotRetrofit by lazy {
        Retrofit(
            url = MIOT_SERVER_URL,
            factories = arrayOf(
                GsonConverterFactory.create(),
                ScalarsConverterFactory.create(),
            ),
            client = miotClient
        )
    }
    private val miotService: MiotService by lazy {
        miotRetrofit.create<MiotService>()
    }
    private val specAttClient by lazy { MiotSpecAttClientImpl() }
    private var _user: MiotUser? = null
        get() = field ?: throw MiotClientException.userNotSet()
    private val user: MiotUser get() = _user!!
    override val Home = MiotClientHome()
    override val Device = MiotClientDevice()

    override fun setUser(user: MiotUser) {
        _user = user
    }

    override suspend fun getUserInfo() = runCatching {
        miotService.getUserInfo(GetUserInfo(user.userId))
    }.recoverCatching {
        throw MiotClientException.getUserInfoFailed(it)
    }

    inner class MiotClientHome : MiotClient.IMiotClientHome {
        override suspend fun getHomes(
            fetchShare: Boolean,
            fetchShareDev: Boolean,
            appVer: Int,
            limit: Int,
        ) = runCatching {
            miotService.getHomes(
                GetHome(
                    appVer, fetchShare, fetchShareDev, false, limit
                )
            )
        }.recoverCatching {
            throw MiotClientException.getHomesFailed(it)
        }

        override suspend fun getDevices(
            home: MiotHome, limit: Int,
        ) = runCatching {
            getDevices(home.uid, home.id.toLong(), limit).getOrThrow()
        }

        override suspend fun getScenes(
            home: MiotHomes.Result.Home,
        ) = runCatching {
            miotService.getScenes(GetScene(home.id.toLong()))
        }.recoverCatching {
            throw MiotClientException.getScenesFailed(it)
        }

        override suspend fun getScenes(
            homeId: Long
        ) = runCatching {
            miotService.getScenes(GetScene(homeId))
        }.recoverCatching {
            throw MiotClientException.getScenesFailed(it)
        }

        override suspend fun getDevices(
            homeOwnerId: Long, homeId: Long, limit: Int,
        ) = runCatching {
            miotService.getDevices(
                GetDevices(homeOwnerId, homeId, limit)
            )
        }.recoverCatching {
            throw MiotClientException.getDevicesFailed(it)
        }

        /**
         * @return 目前不考虑返回结果
         */
        override suspend fun runScene(scene: MiotScenes.Result.Scene) {
            if (scene.icon.isEmpty()) {
                miotService.runScene(RunScene(scene.sceneId.toLong()))
            } else {
                miotService.runScene(RunCommonScene(scene.sceneId.toLong()))
            }
        }
    }

    inner class MiotClientDevice : MiotClient.IMiotClientDevice {
        override suspend fun get(
            device: MiotDevices.Result.Device,
            att: Array<out GetAtt>
        ): Result<DeviceAtt> = runCatching {
            val list = Array(att.size) {
                att[it].run {
                    GetParams.Att(device.did, siid, piid)
                }
            }
            miotService.getDeviceAtt(GetParams(list))
        }.recoverCatching {
            val specType = device.specType ?: throw it
            throw MiotClientException.getSpecAttFailed(specType, it)
        }

        override suspend fun set(device: MiotDevices.Result.Device, att: Array<out SetAtt>) =
            runCatching {
                val list = Array(att.size) {
                    att[it].run {
                        SetParams.Att(device.did, siid, piid, value)
                    }
                }
                miotService.setDeviceAtt(SetParams(list))
                Unit
            }.recoverCatching {
                val specType =
                    device.specType ?: throw MiotDeviceException.specNotFound(device.model)
                throw MiotClientException.getSpecAttFailed(specType, it)
            }

        override suspend fun action(
            device: MiotDevices.Result.Device, siid: Int, aiid: Int, vararg obj: Any
        ) = runCatching {
            miotService.doAction(
                ActionBody(
                    ActionBody.Action(device.did, siid, aiid).apply { `in`.addAll(obj) }
                )
            )
        }.recoverCatching {
            throw MiotClientException.actionFailed(device.did, siid, aiid, *obj, it)
        }

        override suspend fun getSpecAttWithLanguage(
            device: MiotDevices.Result.Device,
            languageCode: String
        ) = specAttClient.getSpecAttWithLanguage(device.specType!!, languageCode)

        override suspend fun getIconUrl(model: String) = withContext(Dispatchers.IO) {
            runCatching {
                val url = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${model}"
                val info = iconClient.get<Info>(url)
                if (info.code != 0) throw MiotClientException.getIconUrlFailed(model)
                info.data.realIcon
            }
        }
    }

    inner class MiotAuthInterceptor(private val user: MiotUser) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originRequest = chain.request()
            val originRequestBody =
                originRequest.body ?: throw IllegalStateException("body is empty")
            val latestRequest: Request
            user.apply {
                if (serviceToken.isEmpty() || securityToken.isEmpty()) {
                    throw IllegalArgumentException("serviceToken or securityToken not found.")
                }
                val data = originRequestBody.readToString()
                val nonce = getNonce()
                val signedNonce = generateSignedNonce(securityToken, nonce)
                val signature = generateSignature(
                    originRequest.url.toString().replace(MIOT_SERVER_URL, "/"),
                    signedNonce,
                    nonce,
                    data
                )
                latestRequest = originRequest.newBuilder().apply {
                    removeHeader("User-Agent")
                    addHeaders(
                        "User-Agent" to MI_HOME_USER_AGENT,
                        "x-xiaomi-protocal-flag-cli" to "PROTOCAL-HTTP2",
                        "Cookie" to "PassportDeviceId=${deviceId};userId=${userId};serviceToken=$serviceToken"
                    )
                    post(
                        FormBody {
                            add("_nonce", nonce)
                            add("data", data)
                            add("signature", signature)
                        })
                }.build()
            }
            return chain.proceed(latestRequest)
        }

        fun generateSignedNonce(secret: String, nonce: String): String {
            val sha = MessageDigest.getInstance("SHA-256")
            sha.update(MiotBase64Impl.decode(secret))
            sha.update(MiotBase64Impl.decode(nonce))
            return MiotBase64Impl.encode(sha.digest())
        }

        fun generateSignature(
            uri: String,
            signedNonce: String,
            nonce: String,
            data: String
        ): String {
            val sign = "$uri&$signedNonce&$nonce&data=$data".toByteArray(StandardCharsets.UTF_8)
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(MiotBase64Impl.decode(signedNonce), "HmacSHA256"))
            val digest = mac.doFinal(sign)
            return MiotBase64Impl.encode(digest)
        }
    }
}