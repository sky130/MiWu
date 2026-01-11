package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import miwu.miot.model.JsonAnySerializer

typealias MiotHome = MiotHomes.Result.Home

@Serializable
data class MiotHomes(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("has_more") val hasMore: Boolean,
        @SerialName("homelist") val homes: List<Home>,
        @SerialName("max_id") val maxId: String,
        @SerialName("share_home_list") val shareHomes: List<Home>? = null,
    ) {
        @Serializable
        data class Home(
            @SerialName("address") val address: String,
            // @SerialName("appear_home_list") val appearHomeList: @Serializable(with = JsonAnySerializer::class) Any,
            @SerialName("background") val background: String,
            @SerialName("bssid") val bssid: String,
            @SerialName("car_did") val carDid: String,
            @SerialName("city_id") val cityId: Int,
            @SerialName("create_time") val createTime: Int,
            @SerialName("dids") val dids: List<String>,
            @SerialName("icon") val icon: String,
            @SerialName("id") val id: String,
            @SerialName("latitude") val latitude: Double,
            @SerialName("longitude") val longitude: Double,
            @SerialName("name") val name: String,
            @SerialName("permit_level") val permitLevel: Int,
            @SerialName("popup_flag") val popupFlag: Int,
            @SerialName("popup_time_stamp") val popupTimeStamp: Int,
            @SerialName("roomlist") val rooms: List<Room>,
            @SerialName("shareflag") val shareFlag: Int,
            @SerialName("smart_room_background") val smartRoomBackground: String,
            @SerialName("status") val status: Int,
            @SerialName("temp_dids") val tempDids: JsonElement,
            @SerialName("uid") val uid: Long
        ) {

            val isShareHome get() = shareFlag != 0

            @Serializable
            data class Room(
                @SerialName("background") val background: String,
                @SerialName("bssid") val bssid: String,
                @SerialName("create_time") val createTime: Int,
                @SerialName("dids") val dids: List<String>,
                @SerialName("icon") val icon: String,
                @SerialName("id") val id: Long,
                @SerialName("name") val name: String,
                @SerialName("parentid") val parentId: String,
                @SerialName("shareflag") val shareflag: Long
            )
        }
    }
}