package miwu.miot.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.model.JsonAnySerializer
import miwu.miot.model.miot.MiotHome

@Serializable
data class GetHome(
    @SerialName("app_ver") val appVer: Int = 7,
    @SerialName("fetch_share") val fetchShare: Boolean = true,
    @SerialName("fetch_share_dev") val fetchShareDev: Boolean = true,
    @SerialName("fg") val fg: Boolean = false,
    @SerialName("limit") val limit: Int = 300,
)

@Serializable
data class GetDevices(
    @SerialName("home_id") val homeId: Long,
    @SerialName("home_owner") val ownerUid: Long,
    @SerialName("limit") val limit: Int = 200,
)

fun MiotHome.toGetDevices(limit: Int = 200) = GetDevices(
    homeId = id.toLong(),
    ownerUid = uid,
    limit = limit,
)

@Serializable
data class GetScene(
    @SerialName("home_id") val homeId: String,
    @SerialName("owner_uid") val ownerUid: String,
    @SerialName("app_version") val appVersion: Int = 12,
    @SerialName("get_type") val getType: Int = 2,
)

@Serializable
data class GetUserInfo(@SerialName("id") val id: String)

@Serializable
data class RunNewScene(
    @SerialName("home_id") val homeId: String,
    @SerialName("owner_uid") val ownerUid: String,
    @SerialName("scene_id") val sceneId: String,
    @SerialName("phone_id") val phoneId: String = "null",
    @SerialName("scene_type") val sceneType: Int = 2,
)

@Serializable
data class RunCommonScene(
    @SerialName("scene_id") val sceneId: Long,
    @SerialName("trigger_key") val triggerKey: String = "user.click",
)

@Serializable
data class RunScene(
    @SerialName("us_id") val usId: Long,
    @SerialName("key") val key: String = "",
)

@Serializable
data class SetParams(@SerialName("params") val params: Array<Att>) {
    @Serializable
    data class Att(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("piid") val piid: Int,
        @SerialName("value") val value: @Serializable(with = JsonAnySerializer::class) Any,
    )

    override fun equals(other: Any?) = other is SetParams && params.contentEquals(other.params)

    override fun hashCode() = params.contentHashCode()
}

@Serializable
data class GetParams(@SerialName("params") val params: Array<Att>) {
    @Serializable
    data class Att(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("piid") val piid: Int,
    )

    override fun equals(other: Any?) = other is GetParams && params.contentEquals(other.params)

    override fun hashCode() = params.contentHashCode()
}

@Serializable
data class ActionBody(@SerialName("params") val params: Action) {
    @Serializable
    data class Action(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("aiid") val aiid: Int,
        @SerialName("in") val `in`: ArrayList<@Serializable(with = JsonAnySerializer::class) Any> = arrayListOf(),
    ) {
        fun body() = ActionBody(this)
    }
}
