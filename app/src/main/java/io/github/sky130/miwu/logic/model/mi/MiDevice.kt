package io.github.sky130.miwu.logic.model.mi

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["homeId"])])
data class MiDevice(
    val roomId: String, // 房间Id
    val roomName: String, // 房间名称
    val homeId: String, // 家庭Id
    val homeName: String, // 房间名称
    val deviceName: String, // 设备名称, 可在米家中修改
    val model: String, // 设备型号
    val did: String, // DeviceId, 设备Id, 用于操纵设备
    val isOnline: Boolean, // 当前设备是否在线
    val specType:String, // 设备的控制类型, 需要依靠这个来判断类型
    val iconUrl: String, // 设备图标链接, 可能为空, 部分设备不存在图标(至少我遇到过
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
