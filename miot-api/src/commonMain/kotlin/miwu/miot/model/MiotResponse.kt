package miwu.miot.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiotResponse<T>(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String = "",
    @SerialName("result") val result: T
)
