package miwu.miot.kmp.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunNewScene(
    @SerialName("home_id")
    val homeId: String,
    @SerialName("owner_uid")
    val ownerUid: String,
    @SerialName("scene_id")
    val sceneId: String,
    @SerialName("phone_id")
    val phoneId: String = "null",
    @SerialName("scene_type")
    val sceneType: Int = 2
)