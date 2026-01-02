package miwu.miot.model.miot


import com.google.gson.annotations.SerializedName

typealias MiotScene = MiotScenes.Result.Scene

data class MiotScenes(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
) {
    data class Result(
        @SerializedName("auto_scene_info_list")
        val autoSceneInfoList: List<Any>,
        @SerializedName("distance_ceiling")
        val distanceCeiling: Int,
        @SerializedName("manual_scene_info_list")
        val scenes: List<Scene>?,
        @SerializedName("voice_scene_info_list")
        val voiceSceneInfoList: List<Any>
    ) {
        data class Scene(
            @SerializedName("authed")
            val authed: List<String>,
            @SerializedName("create_time")
            val createTime: String,
            @SerializedName("enable")
            val enable: Boolean,
            @SerializedName("first_trigger")
            val firstTrigger: String,
            @SerializedName("icon_url")
            val iconUrl: String,
            @SerializedName("last_edit_time")
            val lastEditTime: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("scene_action")
            val sceneAction: SceneAction,
            @SerializedName("scene_else_action")
            val sceneElseAction: String,
            @SerializedName("scene_id")
            val sceneId: String,
            @SerializedName("tags")
            val tags: Tags,
            @SerializedName("template_id")
            val templateId: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("version")
            val version: Int
        ) {
            data class SceneAction(
                @SerializedName("actions")
                val actions: List<Action>,
                @SerializedName("mode")
                val mode: Int
            ) {
                data class Action(
                    @SerializedName("device_group_id")
                    val deviceGroupId: Int,
                    @SerializedName("from")
                    val from: Int,
                    @SerializedName("group_id")
                    val groupId: Int,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("nested_scene_info")
                    val nestedSceneInfo: String,
                    @SerializedName("order")
                    val order: Int,
                    @SerializedName("payload")
                    val payload: String,
                    @SerializedName("protocol_type")
                    val protocolType: Int,
                    @SerializedName("sa_id")
                    val saId: Int,
                    @SerializedName("std_sa_id")
                    val stdSaId: String,
                    @SerializedName("type")
                    val type: Int
                )
            }

            data class Tags(
                @SerializedName("source")
                val source: String,
                @SerializedName("st_id")
                val stId: String
            )
        }
    }
}


//data class MiotScenes(
//    @SerializedName("code")
//    val code: Int,
//    @SerializedName("message")
//    val message: String,
//    @SerializedName("result")
//    val result: Result
//) {
//    data class Result(
//        @SerializedName("common_use_scene")
//        val scenes: List<Scene>?,
//        @SerializedName("has_recommended_template")
//        val hasRecommendedTemplate: Boolean
//    ) {
//        data class Scene(
//            @SerializedName("dids")
//            val dids: List<Any>,
//            @SerializedName("enable")
//            val enable: Boolean,
//            @SerializedName("home_id")
//            val homeId: String,
//            @SerializedName("icon")
//            val icon: String,
//            @SerializedName("pd_ids")
//            val pdIds: List<Any>,
//            @SerializedName("r_type")
//            val rType: Int,
//            @SerializedName("room_id")
//            val roomId: String,
//            @SerializedName("scene_id")
//            val sceneId: String,
//            @SerializedName("scene_name")
//            val sceneName: String,
//            @SerializedName("scene_type")
//            val sceneType: Int,
//            @SerializedName("template_id")
//            val templateId: String,
//            @SerializedName("update_time")
//            val updateTime: String
//        ) {
//            suspend fun Scene.runScene(miot: MiotClient) = miot.Home.runScene(this)
//        }
//    }
//}