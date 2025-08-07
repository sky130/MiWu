package miwu.miot.utils

import com.google.gson.Gson
import miwu.miot.LONG_TEMP_CHARS
import miwu.miot.MiotBase64Impl
import miwu.miot.RANDOM_TEMP_CHARS
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val gson = Gson()

inline fun <reified T> String.to(): T = gson.fromJson(this, T::class.java)

fun String.md5() = MessageDigest.getInstance("MD5")
    .digest(toByteArray())
    .joinToString("") { "%02x".format(it) }
    .uppercase(Locale.ROOT)
    .padEnd(32, '0')


fun getRandomDeviceId(): String =
    (1..16).map { _ -> RANDOM_TEMP_CHARS.random() }.joinToString("")



fun getNonce() = (1..16).map { _ -> LONG_TEMP_CHARS.random() }.joinToString("")



fun String.urlEncode(): String = URLEncoder.encode(this)

fun String.cut() = substring(11)
