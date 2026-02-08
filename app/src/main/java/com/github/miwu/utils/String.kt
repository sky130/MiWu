package com.github.miwu.utils

fun String.mask(
    keepStart: Int = 4,
    keepEnd: Int = 4,
    maskChar: Char = '*'
): String {
    if (isEmpty()) return this
    if (length <= keepStart + keepEnd) {
        return when {
            length <= 2 -> maskChar.toString().repeat(length)
            length <= keepStart -> take(1) + maskChar.toString().repeat(length - 1)
            else -> take(keepStart) + maskChar.toString().repeat(length - keepStart)
        }
    }
    val start = take(keepStart)
    val end = takeLast(keepEnd)
    val maskLength = length - keepStart - keepEnd
    return "$start${maskChar.toString().repeat(maskLength)}$end"
}