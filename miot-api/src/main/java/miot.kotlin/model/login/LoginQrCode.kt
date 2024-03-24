package miot.kotlin.model.login


import com.google.gson.annotations.SerializedName

data class LoginQrCode(
    @SerializedName("code") val code: Int,
    @SerializedName("desc") val desc: String,
    @SerializedName("description") val description: String,
    @SerializedName("loginUrl") val loginUrl: String,
    @SerializedName("lp") val lp: String,
    @SerializedName("qr") val qr: String,
    @SerializedName("result") val result: String,
    @SerializedName("sl") val sl: String,
    @SerializedName("timeInterval") val timeInterval: Int,
    @SerializedName("timeout") val timeout: Int
) {
    fun toQrCode() = QrCode(loginUrl, lp)
}

data class QrCode(
    val data: String, // 二维码数据
    val loginUrl: String // 登录接口
)