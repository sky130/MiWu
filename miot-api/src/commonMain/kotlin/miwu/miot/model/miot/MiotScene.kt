package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiotScene(
    @SerialName("authed") val authed: List<String>,
    @SerialName("create_time") val createTime: String,
    @SerialName("enable") val enable: Boolean,
    @SerialName("first_trigger") val firstTrigger: String? = null,
    @SerialName("icon_url") val iconUrl: String,
    @SerialName("last_edit_time") val lastEditTime: String,
    @SerialName("name") val name: String,
    @SerialName("scene_action") val sceneAction: SceneAction,
    @SerialName("scene_else_action") val sceneElseAction: String? = null,
    @SerialName("scene_id") val sceneId: String,
    @SerialName("tags") val tags: Tags,
    @SerialName("template_id") val templateId: String,
    @SerialName("type") val type: Int,
    @SerialName("version") val version: Int
) {
    @Serializable
    data class Tags(
        @SerialName("source") val source: String? = null,
        @SerialName("st_id") val stId: String? = null
    )


    @Serializable
    data class SceneAction(
        @SerialName("actions") val actions: List<ActionEntry>,
        @SerialName("mode") val mode: Int
    )

    @Serializable
    data class ActionEntry(
        @SerialName("device_group_id") val deviceGroupId: Int,
        @SerialName("from") val from: Int,
        @SerialName("group_id") val groupId: Int,
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String,
        @SerialName("order") val order: Int,
        @SerialName("payload") val payload: String,
        @SerialName("protocol_type") val protocolType: Int,
        @SerialName("sa_id") val saId: Int,
        @SerialName("std_sa_id") val stdSaId: String,
        @SerialName("type") val type: Int
    )
}




