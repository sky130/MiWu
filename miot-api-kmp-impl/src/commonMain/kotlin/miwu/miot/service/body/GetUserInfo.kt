package miwu.miot.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserInfo(
    @SerialName("id") val id: String,
)
