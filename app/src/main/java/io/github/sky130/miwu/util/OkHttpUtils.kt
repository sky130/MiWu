package io.github.sky130.miwu.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.github.sky130.miwu.MainApplication.Companion.context
import io.github.sky130.miwu.logic.network.miot.MiotData
import io.github.sky130.miwu.logic.model.user.LoginMsg
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import okhttp3.FormBody
import okhttp3.RequestBody
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object OkHttpUtils {

    private val client = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()


    fun getRequest(url: String, body: RequestBody? = null): String? {
        if (!isNetSystemUsable()) return null
        val requestBuilder = Request.Builder().url(url).addHeader(
            "User-Agent",
            MiotData.UserAgent
        )

        if (body != null) {
            requestBuilder.post(body)
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            response.body?.string()
        } else {
            null
        }
    }

    fun getRequestResponse(url: String, body: RequestBody? = null): Response? {
        if (!isNetSystemUsable()) return null
        val requestBuilder = Request.Builder().url(url).addHeader(
            "User-Agent",
            MiotData.UserAgent
        )

        if (body != null) {
            requestBuilder.post(body)
        }

        val request = requestBuilder.build()
        return client.newCall(request).execute()
    }

    fun postData(uri: String, data:String, loginMsg: LoginMsg): String? {
        if (!isNetSystemUsable()) return null
        val serviceToken = loginMsg.serviceToken
        val securityToken = loginMsg.securityToken

        if (serviceToken.isEmpty() || securityToken.isEmpty()) {
            println("serviceToken not found, Unauthorized")
            return null
        }
        val tempStr = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val nonce = (1..16)
            .map { _ -> tempStr.random() }
            .joinToString("")
        val signedNonce = generateSignedNonce(securityToken, nonce)
        val signature = generateSignature(uri, signedNonce, nonce, data)
        val body = FormBody.Builder()
            .add("_nonce", nonce)
            .add("data", data)
            .add("signature", signature)
            .build()
        val request = Request.Builder()
            .url("https://api.io.mi.com/app$uri")
            .post(body)
            .header("User-Agent", MiotData.UserAgent)
            .header("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
            .header(
                "Cookie",
                "PassportDeviceId=${loginMsg.deviceId};userId=${loginMsg.userId};serviceToken=$serviceToken;"
            )
            .build()
        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        }catch (e:Exception){
            null
        }
    }


    private fun generateSignedNonce(secret: String, nonce: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        sha.update(Base64.getDecoder().decode(secret))
        sha.update(Base64.getDecoder().decode(nonce))
        return Base64.getEncoder().encodeToString(sha.digest())
    }

    private fun generateSignature(uri: String, signedNonce: String, nonce: String, data: String): String {
        val sign = "$uri&$signedNonce&$nonce&data=$data".toByteArray(StandardCharsets.UTF_8)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(Base64.getDecoder().decode(signedNonce), "HmacSHA256"))
        val digest = mac.doFinal(sign)
        return Base64.getEncoder().encodeToString(digest)
    }

    fun isNetSystemUsable(): Boolean {
        var isNetUsable = false
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (networkCapabilities != null) {
            isNetUsable =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return isNetUsable
    }

}