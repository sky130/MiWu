@file:Suppress("DEPRECATION")

package miot.kotlin.utils

import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import miot.kotlin.MiotManager.base64Encode
import miot.kotlin.MiotManager.base64Decode

fun generateSignedNonce(secret: String, nonce: String): String {
    val sha = MessageDigest.getInstance("SHA-256")
    sha.update(base64Decode(secret))
    sha.update(base64Decode(nonce))
    return base64Encode(sha.digest())
}

fun generateSignature(uri: String, signedNonce: String, nonce: String, data: String): String {
    val sign = "$uri&$signedNonce&$nonce&data=$data".toByteArray(StandardCharsets.UTF_8)
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(base64Decode(signedNonce), "HmacSHA256"))
    val digest = mac.doFinal(sign)
    return base64Encode(digest)
}

fun String.urlEncode(): String = URLEncoder.encode(this)

fun String.request(block: (Request.Builder.() -> Unit)? = null) =
    Request.Builder().url(this).apply {
        block?.let { block() }
    }.build()
