package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * {
 *   "psecurity": "****",
 *   "nonce": ****,
 *   "ssecurity": "***",
 *   "passToken": "V1:D****",
 *   "userId": 1919810,
 *   "cUserId": "****",
 *   "securityStatus": 0,
 *   "notificationUrl": "",
 *   "pwd": 1,
 *   "child": 0,
 *   "miDemo": 0,
 *   "code": 0,
 *   "result": "ok",
 *   "desc": "成功",
 *   "description": "成功",
 *   "location": "***",
 *   "captchaUrl": null
 * }
 */

@Serializable
data class Login(
    @SerialName("code") val code: Int,
    @SerialName("desc") val desc: String,
    @SerialName("location") var location: String,
    @SerialName("userId") val userId: Long,
    @SerialName("ssecurity") var ssecurity: String,
    @SerialName("cUserId") val cUserId: String,
    @SerialName("captchaUrl") val captchaUrl: String? = null,
    @SerialName("child") val child: Int,
    @SerialName("miDemo") val miDemo: Int,
    @SerialName("nonce") val nonce: String,
    @SerialName("description") val description: String,
    @SerialName("notificationUrl") val notificationUrl: String,
    @SerialName("passToken") val passToken: String,
    @SerialName("psecurity") val psecurity: String,
    @SerialName("pwd") val pwd: Int,
    @SerialName("result") val result: String,
    @SerialName("securityStatus") val securityStatus: Int,
)
