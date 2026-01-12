package miwu.miot.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetDevices(
    @SerialName("home_owner")
    val homeOwner: Long,
    @SerialName("home_id")
    val homeId: Long,
    @SerialName("limit")
    val limit: Int = 200
)