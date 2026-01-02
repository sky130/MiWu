package miwu.miot.service.body

import com.google.gson.annotations.SerializedName

data class RunNewScene(
    @SerializedName("home_id")
    val homeId: String,
    @SerializedName("owner_uid")
    val ownerUid: String,
    @SerializedName("scene_id")
    val sceneId: String,
    @SerializedName("phone_id")
    val phoneId: String = "null",
    @SerializedName("scene_type")
    val sceneType: Int = 2
)