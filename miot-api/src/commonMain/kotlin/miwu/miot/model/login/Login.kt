package miwu.miot.model.login

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Login(
    @SerialName("code") val code: Int,
    @SerialName("desc") val desc: String,
    @SerialName("location") var location: String,
    @SerialName("userId") val userId: Long,
    @SerialName("ssecurity") var securityToken: String
)
