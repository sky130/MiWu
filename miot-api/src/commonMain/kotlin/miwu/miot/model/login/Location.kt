package miwu.miot.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("qs") val qs: String,
    @SerialName("sid") val sid: String,
    @SerialName("_sign") val sign: String,
    @SerialName("callback") val callback: String,
    @SerialName("location") val location: String,
    @SerialName("ssecurity") val ssecurity: String
)
