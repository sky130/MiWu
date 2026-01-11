package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginQrCode(
    @SerialName("code") val code: Int,
    @SerialName("desc") val desc: String,
    @SerialName("description") val description: String,
    @SerialName("loginUrl") val loginUrl: String,
    @SerialName("lp") val lp: String,
    @SerialName("qr") val qr: String,
    @SerialName("result") val result: String,
    @SerialName("sl") val sl: String,
    @SerialName("timeInterval") val timeInterval: Int,
    @SerialName("timeout") val timeout: Int
) {
    fun toQrCode() = QrCode(loginUrl, lp)

    @Serializable
    data class QrCode(
        val data: String, // 二维码数据
        val loginUrl: String // 登录接口
    )
}

