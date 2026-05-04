package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceList(
    @SerialName("home_info") val homeInfo: DeviceHome? = null,
    @SerialName("device_info") val deviceInfo: List<MiotDevice>? = null,
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("max_did") val maxDid: String
)

@Serializable
data class DeviceHome(
    @SerialName("id") val id: Long,
    @SerialName("roomlist") val room: List<DeviceRoom>
)

@Serializable
data class DeviceRoom(
    @SerialName("dids") val dids: List<String>,
    @SerialName("id") val id: Long
)

@Serializable
data class DeviceInfoResponse(
    @SerialName("code") val code: Int,
    @SerialName("data") val data: DeviceInfoData
)

@Serializable
data class DeviceInfoData(
    @SerialName("realIcon") val realIcon: String
)
