package miwu.miot.common

fun getRandomDeviceId() = (1..16)
    .map { RANDOM_TEMP_CHARS.random() }
    .joinToString("")

fun getNonce() = (1..16)
    .map { LONG_TEMP_CHARS.random() }
    .joinToString("")

fun String.removePrefix() = substring(11)