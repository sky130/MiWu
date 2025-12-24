package com.github.miwu.ktx

fun String.mask(
    keepStart: Int = 4,
    keepEnd: Int = 4,
    maskChar: Char = '*'
): String {
    if (this.isEmpty()) return this
    if (this.length <= keepStart + keepEnd) {
        return when {
            this.length <= 2 -> maskChar.toString().repeat(this.length)
            this.length <= keepStart -> this.take(1) + maskChar.toString().repeat(this.length - 1)
            else -> this.take(keepStart) + maskChar.toString().repeat(this.length - keepStart)
        }
    }
    val start = this.take(keepStart)
    val end = this.takeLast(keepEnd)
    val maskLength = this.length - keepStart - keepEnd
    return "$start${maskChar.toString().repeat(maskLength)}$end"
}