package com.github.miwu.logic.database.model


import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotDevices.Result.Device

@Keep
@Entity(tableName = "miwu_device")
data class MiwuDevice(
    val bssid: String,
    val cnt: Int,
    val comFlag: Int,
    val did: String,
    @Embedded
    val extra: Extra,
    val freqFlag: Boolean,
    val hideMode: Int,
    val isOnline: Boolean,
    val lastOnline: Long,
    val latitude: String,
    val localIp: String?,
    val longitude: String,
    val mac: String,
    val model: String,
    val name: String,
    val orderTime: Int,
    @Embedded
    val owner: Owner,
    val parentId: String?,
    val permitLevel: Int,
    val pid: Int,
    val rssi: Int,
    val showMode: Int,
    val specType: String?,
    val ssid: String?,
    val token: String,
    val uid: Long
) {
    @PrimaryKey()
    var index: Int = 0

    companion object {
        fun Device.toMiwu() = MiwuDevice(
            bssid = this.bssid,
            cnt = this.cnt,
            comFlag = this.comFlag,
            did = this.did,
            extra = Extra(
                fwVersion = this.extra.fwVersion,
                isSetPinCode = this.extra.isSetPinCode,
                isSubGroup = this.extra.isSubGroup,
                mcuVersion = this.extra.mcuVersion,
                pinCodeType = this.extra.pinCodeType,
                platform = this.extra.platform,
                showGroupMember = this.extra.showGroupMember
            ),
            freqFlag = this.freqFlag,
            hideMode = this.hideMode,
            isOnline = this.isOnline,
            lastOnline = this.lastOnline,
            latitude = this.latitude,
            localIp = this.localIp,
            longitude = this.longitude,
            mac = this.mac,
            model = this.model,
            name = this.name,
            orderTime = this.orderTime,
            owner = Owner(this.owner.nickname, this.owner.userid),
            parentId = this.parentId,
            permitLevel = this.permitLevel,
            pid = this.pid,
            rssi = this.rssi,
            showMode = this.showMode,
            specType = this.specType,
            ssid = this.ssid,
            token = this.token,
            uid = this.uid
        )

        fun MiwuDevice.toMiot() = Device(
            bssid = this.bssid,
            cnt = this.cnt,
            comFlag = this.comFlag,
            did = this.did,
            extra = Device.Extra(
                fwVersion = this.extra.fwVersion,
                isSetPinCode = this.extra.isSetPinCode,
                isSubGroup = this.extra.isSubGroup,
                mcuVersion = this.extra.mcuVersion,
                pinCodeType = this.extra.pinCodeType,
                platform = this.extra.platform,
                showGroupMember = this.extra.showGroupMember
            ),
            freqFlag = this.freqFlag,
            hideMode = this.hideMode,
            isOnline = this.isOnline,
            lastOnline = this.lastOnline,
            latitude = this.latitude,
            localIp = this.localIp,
            longitude = this.longitude,
            mac = this.mac,
            model = this.model,
            name = this.name,
            orderTime = this.orderTime,
            owner = Device.Owner(this.owner.nickname, this.owner.userid),
            parentId = this.parentId,
            permitLevel = this.permitLevel,
            pid = this.pid,
            rssi = this.rssi,
            showMode = this.showMode,
            specType = this.specType,
            ssid = this.ssid,
            token = this.token,
            uid = this.uid
        )
    }


    data class Extra(
        val fwVersion: String?,
        val isSetPinCode: Int?,
        val isSubGroup: Boolean?,
        val mcuVersion: String?,
        val pinCodeType: Int?,
        val platform: String?,
        val showGroupMember: Boolean?,
    )

    data class Owner(
        val nickname: String,
        val userid: Long
    )
}
