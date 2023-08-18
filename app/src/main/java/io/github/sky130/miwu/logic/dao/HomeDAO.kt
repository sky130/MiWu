package io.github.sky130.miwu.logic.dao

import android.content.Context
import io.github.sky130.miwu.MainApplication
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
        miInfo = MiotService.getMiInfo()
    }

    fun isInit() = miInfo != null

    fun homeSize() = miInfo?.homeList?.size
    fun getHomeIndex() = edit.get("home_index", 0)
    fun setHomeIndex(index: Int) = edit.get("home_index", index)
    fun getHome(index: Int = getHomeIndex()) = miInfo?.homeList?.get(index)


}