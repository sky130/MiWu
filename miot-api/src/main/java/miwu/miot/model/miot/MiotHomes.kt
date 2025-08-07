package miwu.miot.model.miot


import com.google.gson.annotations.SerializedName

typealias MiotHome = MiotHomes.Result.Home

data class MiotHomes(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result
) {
    data class Result(
        @SerializedName("has_more") val hasMore: Boolean,
        @SerializedName("homelist") val homes: List<Home>,
        @SerializedName("max_id") val maxId: String,
        @SerializedName("share_home_list")  val shareHomes: List<Home>?,
    ) {
        data class Home(
            @SerializedName("address") val address: String,
            @SerializedName("appear_home_list") val appearHomeList: Any,
            @SerializedName("background") val background: String,
            @SerializedName("bssid") val bssid: String,
            @SerializedName("car_did") val carDid: String,
            @SerializedName("city_id") val cityId: Int,
            @SerializedName("create_time") val createTime: Int,
            @SerializedName("dids") val dids: List<Any>,
            @SerializedName("icon") val icon: String,
            @SerializedName("id") val id: String,
            @SerializedName("latitude") val latitude: Double,
            @SerializedName("longitude") val longitude: Double,
            @SerializedName("name") val name: String,
            @SerializedName("permit_level") val permitLevel: Int,
            @SerializedName("popup_flag") val popupFlag: Int,
            @SerializedName("popup_time_stamp") val popupTimeStamp: Int,
            @SerializedName("roomlist") val rooms: List<Room>,
            @SerializedName("shareflag") val shareFlag: Int,
            @SerializedName("smart_room_background") val smartRoomBackground: String,
            @SerializedName("status") val status: Int,
            @SerializedName("temp_dids") val tempDids: Any,
            @SerializedName("uid") val uid: Long
        ) {

            val isShareHome get() =  shareFlag != 0

            data class Room(
                @SerializedName("background") val background: String,
                @SerializedName("bssid") val bssid: String,
                @SerializedName("create_time") val createTime: Int,
                @SerializedName("dids") val dids: List<String>,
                @SerializedName("icon") val icon: String,
                @SerializedName("id") val id: Long,
                @SerializedName("name") val name: String,
                @SerializedName("parentid") val parentId: String,
                @SerializedName("shareflag") val shareflag: Long
            )
        }
    }
}