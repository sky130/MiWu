package io.github.sky130.miwu.logic.model.mi

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.sky130.miwu.logic.dao.database.AppDatabase


// 用于存储对应家庭的信息
data class MiHome(
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称,在米家中可修改
    val userId: String, // 用户Id
    val isShareHome: Boolean, // 是否为共享家庭
    val sceneList: ArrayList<MiScene>, // 情景控制
    val roomList: ArrayList<MiRoom>, // 房间列表
    val deviceList: ArrayList<MiDevice>, // 全屋设备列表
) {
    fun toEntity() = MiHomeEntity(homeId, homeName, userId, isShareHome)
}

@Entity(tableName = "MiHome", indices = [Index(value = ["homeId"], unique = true)])
data class MiHomeEntity(
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称
    val userId: String, // 用户Id
    val isShareHome: Boolean, // 是否为共享家庭
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun toMiHome(): MiHome {
        val database = AppDatabase.getDatabase()
        val deviceList = ArrayList(database.deviceDAO().getDeviceFromHome(homeId))
        deviceList.sortBy { !it.isOnline }
        val roomList = ArrayList<MiRoom>().apply {
            database.roomDAO().getRoom(homeId).forEach {
                add(it.toMiRoom())
            }
        }
        val sceneList = ArrayList(database.sceneDAO().getScene(homeId))
        return MiHome(homeId, homeName, userId, isShareHome, sceneList, roomList, deviceList)
    }

}


