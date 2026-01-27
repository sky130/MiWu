package miwu.miot.utils

import okio.ByteString.Companion.encodeUtf8
import java.net.URLEncoder
import kotlin.io.encoding.Base64

inline fun <reified T> String.to(): Result<T> = runCatching {
    json.decodeFromString<T>(this)
}

fun String.md5() = this.encodeUtf8()
    .md5()
    .hex()
    .uppercase()
    .padEnd(32, '0')

fun String.urlEncode(): String = URLEncoder.encode(this)
