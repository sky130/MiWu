package io.github.sky130.miwu.logic.dao

import android.content.Context
import io.github.sky130.miwu.MainApplication
import io.github.sky130.miwu.logic.dao.database.AppDatabase
import io.github.sky130.miwu.logic.model.mi.MiHomeEntity
import io.github.sky130.miwu.logic.model.mi.MiInfo
import io.github.sky130.miwu.logic.network.MiotService
import io.github.sky130.miwu.util.SettingUtils

// 用于管理当前的家庭
object HomeDAO {

    private val sharedPreferences =
        MainApplication.context.getSharedPreferences("home", Context.MODE_PRIVATE)!!
    private var miInfo: MiInfo? = null

    private val edit = SettingUtils(sharedPreferences)

    fun init() {
        if (isDatabaseInit()) {
            miInfo = getMiInfo()
        } else {
            miInfo = MiotService.getMiInfo() ?: return
            AppDatabase.getDatabase().apply {
                homeDAO().addHomes(miInfo!!.homeList.map { it.toEntity() })
                miInfo!!.homeList.forEach { sceneDAO().addScenes(it.sceneList) }
                miInfo!!.homeList.forEach { deviceDAO().addDevices(it.deviceList) }
                miInfo!!.homeList.forEach { home -> roomDAO().addRooms(home.roomList.map { it.toEntity() }) }
            }
        }
    }


    // 刷新设备在线
    fun resetDeviceOnline(block: () -> Unit) {
        if (!isInit()) return block()
        miInfo!!.homeList.forEach { home ->
            val list = MiotService.getHomeDevice(home.userId, home.homeId) ?: return@forEach
            list.forEach { homeDevice ->
                home.deviceList.forEach {
                    if (homeDevice.did == it.did)
                        it.isOnline = homeDevice.isOnline
                }
            }
            AppDatabase.getDatabase().deviceDAO().addDevices(home.deviceList)
        }
        init()
        block()
    }

    private fun getMiInfo() =
        MiInfo(ArrayList(AppDatabase.getDatabase().homeDAO().getAllHome().map { it.toMiHome() }))

    fun isDatabaseInit() = AppDatabase.getDatabase().homeDAO().getCount() > 0
    fun isInit() = miInfo != null
    fun homeSize() = miInfo?.homeList?.size
    fun getHomeIndex() = edit.get("home_index", 0)
    fun setHomeIndex(index: Int) = edit.get("home_index", index)
    fun getHome(index: Int = getHomeIndex()) = miInfo?.homeList?.get(index)


}