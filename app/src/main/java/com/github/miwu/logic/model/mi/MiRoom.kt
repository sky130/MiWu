package com.github.miwu.logic.model.mi

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.github.miwu.logic.dao.database.AppDatabase


// 用于存储一个房间
data class MiRoom(
    val roomId: String, // 房间Id
    val roomName: String, // 房间名称,在米家中可以修改
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称
    val deviceIdList: ArrayList<String>, // 设备Id列表
    val deviceList: ArrayList<MiDevice>, // 存储当前房间下的所有设备
) {
    fun toEntity() = MiRoomEntity(roomId, roomName, homeId, homeName)
}

@Entity(tableName = "MiRoom",indices = [Index(value = ["roomId"], unique = true)])
data class MiRoomEntity(
    val roomId: String, // 房间Id
    val roomName: String, // 房间名称,在米家中可以修改
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun toMiRoom(): MiRoom {
        val database = AppDatabase.getDatabase()
        val deviceList = ArrayList(database.deviceDAO().getDeviceFromRoom(roomId))
        val deviceIdList = ArrayList<String>().apply {
            deviceList.forEach { add(it.did) }
        }
        return MiRoom(roomId, roomName, homeId, homeName, deviceIdList, deviceList)
    }

}