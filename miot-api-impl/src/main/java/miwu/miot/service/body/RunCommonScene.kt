package miwu.miot.service.body


import com.google.gson.annotations.SerializedName

data class RunCommonScene(
    @SerializedName("scene_id")
    val sceneId: Long,
    @SerializedName("trigger_key")
    val triggerKey: String = "user.click"
)