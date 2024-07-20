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

fun generateSignedNonce(secret: String, nonce: String): String {
    val sha = MessageDigest.getInstance("SHA-256")
    sha.update(base64Dcode(secret))
    sha.update(base64Dcode(nonce))
    return base64Encode(sha.digest())
}

fun generateSignature(uri: String, signedNonce: String, nonce: String, data: String): String {
    val sign = "$uri&$signedNonce&$nonce&data=$data".toByteArray(StandardCharsets.UTF_8)
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(base64Dncode(signedNonce), "HmacSHA256"))
    val digest = mac.doFinal(sign)
    return base64Encode(digest)
}

fun String.urlEncode(): String = URLEncoder.encode(this)

fun String.request(block: (Request.Builder.() -> Unit)? = null) =
    Request.Builder().url(this).apply {
        block?.let { block() }
    }.build()
