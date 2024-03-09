package miot.kotlin.model.miot


import com.google.gson.annotations.SerializedName

data class MiotScenes(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
) {
    data class Result(
        @SerializedName("common_use_scene")
        val scenes: List<Scene>?,
        @SerializedName("has_recommended_template")
        val hasRecommendedTemplate: Boolean
    ) {
        data class Scene(
            @SerializedName("dids")
            val dids: List<Any>,
            @SerializedName("enable")
            val enable: Boolean,
            @SerializedName("home_id")
            val homeId: String,
            @SerializedName("icon")
            val icon: String,
            @SerializedName("pd_ids")
            val pdIds: List<Any>,
            @SerializedName("r_type")
            val rType: Int,
            @SerializedName("room_id")
            val roomId: String,
            @SerializedName("scene_id")
            val sceneId: String,
            @SerializedName("scene_name")
            val sceneName: String,
            @SerializedName("scene_type")
            val sceneType: Int,
            @SerializedName("template_id")
            val templateId: String,
            @SerializedName("update_time")
            val updateTime: String
        )
    }
}