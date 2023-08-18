package io.github.sky130.miwu.logic.network.miot

import kotlin.random.Random

object MiotData {
    const val UserAgent = "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS"
    const val MISID = "xiaomiio"

    fun getRandomDeviceId(): String {
        val tempStr = "0123456789ABCDEF"
        val builder = StringBuilder()
        for (i in 0 until 16) {
            builder.append(tempStr[Random.nextInt(0, tempStr.length)])
        }
        return builder.toString()
    }

    fun buildCookies(deviceId: String): String {
        return "deviceId=$deviceId; sdkVersion=3.4.1"
    }
}