package com.github.miwu.logic.database.model


import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDeviceExtra

@Keep
@Entity(tableName = "favorite_device")
data class MiwuDatabaseDevice(
    val bssid: String,
    val cnt: Int?,
    val comFlag: Int,
    val did: String,
    @Embedded
    val extra: Extra,
    val freqFlag: Boolean,
    val hideMode: Int,
    val isOnline: Boolean,
    val lastOnline: Long?,
    val latitude: String,
    val localIp: String?,
    val longitude: String,
    val mac: String,
    val model: String,
    val name: String,
    val orderTime: Int,
    val parentId: String?,
    val permitLevel: Int,
    val pid: Int,
    val rssi: Int?,
    val showMode: Int,
    val specType: String?,
    val ssid: String?,
    val token: String,
    val uid: Long
) {
    @PrimaryKey()
    var position: Int = 0

    companion object {
        fun MiotDevice.toMiwu() = MiwuDatabaseDevice(
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

        fun MiwuDatabaseDevice.toMiot() = MiotDevice(
            bssid = this.bssid,
            cnt = this.cnt,
            comFlag = this.comFlag,
            did = this.did,
            extra = MiotDeviceExtra(
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
        val nickname: String?,
        val userid: Long?
    )
}
