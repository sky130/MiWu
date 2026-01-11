package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QrCodeSid(
    @SerialName("location") val location: String,
    @SerialName("ssecurity") val securityToken: String
)
