package miwu.miot.utils

import io.ktor.http.encodeURLQueryComponent
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import miwu.miot.LONG_TEMP_CHARS
import miwu.miot.RANDOM_TEMP_CHARS
import miwu.miot.ktx.json
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.use
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

inline fun <reified T> String.to(): T = json.decodeFromString<T>(this)

fun String.md5() = this.encodeUtf8()
    .md5()
    .hex()
    .uppercase()
    .padEnd(32, '0')

fun getRandomDeviceId(): String =
    (1..16).map { _ -> RANDOM_TEMP_CHARS.random() }.joinToString("")

fun getNonce() = (1..16).map { _ -> LONG_TEMP_CHARS.random() }.joinToString("")

fun String.urlEncode(): String = this.encodeURLQueryComponent()

fun String.cut() = substring(11)
