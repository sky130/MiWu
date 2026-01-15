package miwu.miot.kmp.utils

import okio.ByteString.Companion.encodeUtf8

inline fun <reified T> String.to(): T = json.decodeFromString<T>(this)

fun String.md5() = this.encodeUtf8()
    .md5()
    .hex()
    .uppercase()
    .padEnd(32, '0')
