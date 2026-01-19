package miwu.miot.interceptor

import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.common.MI_HOME_USER_AGENT
import miwu.miot.common.getNonce
import miwu.miot.model.MiotUser
import miwu.miot.utils.FormBody
import miwu.miot.utils.addHeaders
import miwu.miot.utils.readToString
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

class MiotAuthInterceptor(private val user: MiotUser) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val originRequestBody =
            originRequest.body ?: throw IllegalStateException("body is empty")

        val latestRequest: Request
        user.apply {
            if (serviceToken.isEmpty() || ssecurity.isEmpty()) {
                throw IllegalArgumentException("serviceToken or securityToken not found.")
            }
            val data = originRequestBody.readToString()
            val nonce = getNonce()
            val signedNonce = generateSignedNonce(ssecurity, nonce)
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
            return chain.proceed(latestRequest)
        }
    }

    fun generateSignedNonce(secret: String, nonce: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        sha.update(Base64.Default.decode(secret))
        sha.update(Base64.Default.decode(nonce))
        return Base64.Default.encode(sha.digest())
    }

    fun generateSignature(
        uri: String,
        signedNonce: String,
        nonce: String,
        data: String
    ): String {
        val sign = "$uri&$signedNonce&$nonce&data=$data".toByteArray(StandardCharsets.UTF_8)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(Base64.Default.decode(signedNonce), "HmacSHA256"))
        val digest = mac.doFinal(sign)
        return Base64.Default.encode(digest)
    }

}