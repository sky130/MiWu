package io.github.sky130.miwu.logic.model.mi

import com.google.gson.annotations.SerializedName

data class MiScene(
    @SerializedName("scene_id") val sceneId: String,
    @SerializedName("scene_name") val sceneName: String,
    val icon: String,
    var homeId:String ="",
)