package io.github.sky130.miwu.logic.model.mi


// 用于存储一个房间
data class MiRoom(
    val roomId: String, // 房间Id
    val roomName: String, // 房间名称,在米家中可以修改
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称
    val deviceIdList:ArrayList<String>, // 设备Id列表
    val deviceList: ArrayList<MiDevice>, // 存储当前房间下的所有设备
)