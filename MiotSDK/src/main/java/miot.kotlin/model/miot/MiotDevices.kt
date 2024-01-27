package miot.kotlin.model.miot


import com.google.gson.annotations.SerializedName

data class MiotDevices(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result
) {
    data class Result(
        @SerializedName("home_info") val homeInfo: HomeInfo?,
        @SerializedName("device_info") val deviceInfo: List<Device>?,
        @SerializedName("has_more") val hasMore: Boolean,
        @SerializedName("max_did") val maxDid: String
    ) {
        data class Device(
            @SerializedName("bssid") val bssid: String,
            @SerializedName("cnt") val cnt: Int,
            @SerializedName("comFlag") val comFlag: Int,
            @SerializedName("did") val did: String,
            @SerializedName("extra") val extra: Extra,
            @SerializedName("freqFlag") val freqFlag: Boolean,
            @SerializedName("hide_mode") val hideMode: Int,
            @SerializedName("isOnline") val isOnline: Boolean,
            @SerializedName("last_online") val lastOnline: Long,
            @SerializedName("latitude") val latitude: String,
            @SerializedName("localip") val localIp: String?,
            @SerializedName("longitude") val longitude: String,
            @SerializedName("mac") val mac: String,
            @SerializedName("model") val model: String,
            @SerializedName("name") val name: String,
            @SerializedName("orderTime") val orderTime: Int,
            @SerializedName("owner") val owner: Owner,
            @SerializedName("parent_id") val parentId: String?,
            @SerializedName("permitLevel") val permitLevel: Int,
            @SerializedName("pid") val pid: Int,
            @SerializedName("rssi") val rssi: Int,
            @SerializedName("show_mode") val showMode: Int,
            @SerializedName("spec_type") val specType: String?,
            @SerializedName("ssid") val ssid: String?,
            @SerializedName("token") val token: String,
            @SerializedName("uid") val uid: Long
        ) {
            data class Extra(
                @SerializedName("fw_version") val fwVersion: String?,
                @SerializedName("isSetPincode") val isSetPinCode: Int?,
                @SerializedName("isSubGroup") val isSubGroup: Boolean?,
                @SerializedName("mcu_version") val mcuVersion: String?,
                @SerializedName("pincodeType") val pinCodeType: Int?,
                @SerializedName("platform") val platform: String?,
                @SerializedName("showGroupMember") val showGroupMember: Boolean?,
            )

            data class Owner(
                @SerializedName("nickname") val nickname: String,
                @SerializedName("userid") val userid: Int
            )
        }

        data class HomeInfo(
            @SerializedName("dids") val dids: Any,
            @SerializedName("id") val id: Long,
            @SerializedName("roomlist") val room: List<Room>
        ) {
            data class Room(
                @SerializedName("dids") val dids: List<String>, @SerializedName("id") val id: Long
            )
        }
    }
}