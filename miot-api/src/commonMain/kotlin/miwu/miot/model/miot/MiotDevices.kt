package miwu.miot.model.miot


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.MiotManager
import miwu.miot.exception.MiotDeviceException


typealias MiotDevice = MiotDevices.Result.Device
typealias MiotDeviceExtra = MiotDevices.Result.Device.Extra

@Serializable
data class MiotDevices(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("home_info") val homeInfo: HomeInfo? = null,
        @SerialName("device_info") val deviceInfo: List<Device>? = null,
        @SerialName("has_more") val hasMore: Boolean,
        @SerialName("max_did") val maxDid: String
    ) {
        @Serializable
        data class Device(
            @SerialName("bssid") val bssid: String,
            @SerialName("cnt") val cnt: Int? = null,
            @SerialName("comFlag") val comFlag: Int,
            @SerialName("did") val did: String,
            @SerialName("extra") val extra: Extra,
            @SerialName("freqFlag") val freqFlag: Boolean,
            @SerialName("hide_mode") val hideMode: Int,
            @SerialName("isOnline") val isOnline: Boolean,
            @SerialName("last_online") val lastOnline: Long? = null,
            @SerialName("latitude") val latitude: String,
            @SerialName("localip") val localIp: String? = null,
            @SerialName("longitude") val longitude: String,
            @SerialName("mac") val mac: String,
            @SerialName("model") val model: String,
            @SerialName("name") val name: String,
            @SerialName("orderTime") val orderTime: Int,
            @SerialName("parent_id") val parentId: String? = null,
            @SerialName("permitLevel") val permitLevel: Int,
            @SerialName("pid") val pid: Int,
            @SerialName("rssi") val rssi: Int? = 0,
            @SerialName("show_mode") val showMode: Int,
            @SerialName("spec_type") val specType: String? = null,
            @SerialName("ssid") val ssid: String? = null,
            @SerialName("token") val token: String,
            @SerialName("uid") val uid: Long
        ) {
            @Serializable
            data class Extra(
                @SerialName("fw_version") val fwVersion: String? = null,
                @SerialName("isSetPincode") val isSetPinCode: Int? = null,
                @SerialName("isSubGroup") val isSubGroup: Boolean? = null,
                @SerialName("mcu_version") val mcuVersion: String? = null,
                @SerialName("pincodeType") val pinCodeType: Int? = null,
                @SerialName("platform") val platform: String? = null,
                @SerialName("showGroupMember") val showGroupMember: Boolean? = null,
            )

            suspend fun getSpecAtt(manager: MiotManager) = runCatching {
                val specType = specType ?: throw MiotDeviceException.specNotFound(model)
                manager.SpecAtt.getSpecAtt(specType).getOrThrow()
            }.onFailure {
                it.printStackTrace()
            }

            suspend fun getSpecAttLanguageMap(
                manager: MiotManager,
                languageCode: String = "zh_cn"
            ): kotlin.Result<Map<String, String>> = runCatching {
                val specType = specType ?: throw MiotDeviceException.specNotFound(model)
                val language = manager.SpecAtt.getSpecMultiLanguage(specType).getOrThrow()
                manager.SpecAtt.getSpecAttLanguageMap(language, languageCode).getOrThrow()
            }
        }

        @Serializable
        data class Info(
            @SerialName("code") val code: Int,
            @SerialName("data") val data: Data
        ) {
            @Serializable
            data class Data(@SerialName("realIcon") val realIcon: String)
        }
    }

    @Serializable
    data class HomeInfo(
        // @SerialName("dids") val dids: Any,
        @SerialName("id") val id: Long,
        @SerialName("roomlist") val room: List<Room>
    ) {
        @Serializable
        data class Room(
            @SerialName("dids") val dids: List<String>, @SerialName("id") val id: Long
        )
    }
}