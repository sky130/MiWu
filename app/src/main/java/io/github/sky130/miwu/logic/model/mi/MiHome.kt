package io.github.sky130.miwu.logic.model.mi


// 用于存储对应家庭的信息
data class MiHome(
    val homeId: String, // 家庭Id
    val homeName: String, // 家庭名称,在米家中可修改
    val userId: String, // 用户Id
    val isShareHome: Boolean, // 是否为共享家庭
    val sceneList:ArrayList<MiScene>, // 情景控制
    val roomList: ArrayList<MiRoom>, // 房间列表
    val deviceList:ArrayList<MiDevice> // 全屋设备列表
)
