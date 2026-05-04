package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeList(
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("homelist") val homes: List<MiotHome>,
    @SerialName("max_id") val maxId: String,
    @SerialName("share_home_list") val shareHomes: List<MiotHome>? = null,
)
