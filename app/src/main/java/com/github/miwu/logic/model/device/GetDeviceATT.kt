package com.github.miwu.logic.model.device

// 通过这个数据类可以获取设备当前信息
data class GetDeviceATT(
    val code: String,
    val iid: String,
    val siid: Int,
    val piid: Int,
    val value: Any,
    val updateTime: Long,
    val exeTime: Int,
) {
    inline fun <reified T> getValue(defaultValue: T): T {
        try {
            return when (T::class) {
                Boolean::class -> {
                    value as T
                }

                Int::class -> {
                    value.toString().toInt() as T
                }

                Long::class -> {
                    value.toString().toLong() as T
                }

                String::class -> {
                    value.toString() as T
                }

                Float::class -> {
                    value.toString().toFloat() as T
                }

                Double::class -> {
                    value.toString().toDouble() as T
                }

                else -> {
                    defaultValue
                }
            }
        } catch (e: Exception) {
            return defaultValue
        }

    }
}