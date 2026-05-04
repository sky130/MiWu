package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiotRoom(
    @SerialName("background") val background: String,
    @SerialName("bssid") val bssid: String,
    @SerialName("create_time") val createTime: Int,
    @SerialName("dids") val dids: List<String>,
    @SerialName("icon") val icon: String,
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("parentid") val parentId: String,
    @SerialName("shareflag") val shareFlag: Long
)