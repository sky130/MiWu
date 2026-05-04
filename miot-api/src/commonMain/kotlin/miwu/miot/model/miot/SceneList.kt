package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SceneList(
    @SerialName("distance_ceiling") val distanceCeiling: Int,
    @SerialName("manual_scene_info_list") val scenes: List<MiotScene>? = null,
)
