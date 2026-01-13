package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginQrCode(
    @SerialName("code") val code: Int,
    @SerialName("desc") val desc: String,
    @SerialName("description") val description: String,
    @SerialName("loginUrl") val loginUrl: String? = null,
    @SerialName("lp") val lp: String? = null,
    @SerialName("qr") val qr: String? = null,
    @SerialName("result") val result: String,
    @SerialName("sl") val sl: String? = null,
    @SerialName("timeInterval") val timeInterval: Int? = null,
    @SerialName("timeout") val timeout: Int? = null
) {
    fun toQrCode(): QrCode? {
        if (loginUrl == null || lp == null) return null
        return QrCode(loginUrl, lp)
    }

    @Serializable
    data class QrCode(
        val data: String, // 二维码数据
        val loginUrl: String // 登录接口
    )
}

