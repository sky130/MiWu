package miwu.miot.service.body


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunCommonScene(
    @SerialName("scene_id")
    val sceneId: Long,
    @SerialName("trigger_key")
    val triggerKey: String = "user.click"
)