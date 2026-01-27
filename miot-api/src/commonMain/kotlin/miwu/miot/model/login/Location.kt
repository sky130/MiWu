package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.exception.MiotAuthException

@Serializable
data class Location(
    @SerialName("code") val code: Int,
    @SerialName("qs") val qs: String,
    @SerialName("sid") val sid: String,
    @SerialName("_sign") val sign: String,
    @SerialName("callback") val callback: String,
    @SerialName("location") val location: String,
    @SerialName("ssecurity") val ssecurity: String
) {
    fun getOrThrowAuthException(): Location {
        return when (code) {
            20003 -> throw MiotAuthException("Invalid UserName")
            22009 -> throw MiotAuthException("Package Name Denied Exception")
            70002 -> throw MiotAuthException.invalidCredentials()
            70016 -> throw MiotAuthException.invalidCredentials()
            81003 -> throw MiotAuthException.needVerification()
            87001 -> throw MiotAuthException.needVerification()
            else -> this
        }
    }
}
