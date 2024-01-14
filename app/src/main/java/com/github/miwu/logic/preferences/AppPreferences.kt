package com.github.miwu.logic.preferences

import com.github.miwu.MainApplication.Companion.gson
import com.github.miwu.miot.quick.MiotBaseQuick
import com.google.gson.annotations.SerializedName
import kndroidx.preference.PreferencesX
import miot.kotlin.MiotManager

object AppPreferences : PreferencesX("app") {

    var userId by stringPreference("userId", "")

    var securityToken by stringPreference("securityToken", "")

    var serviceToken by stringPreference("serviceToken", "")

    val deviceId by lazy { MiotManager.getRandomDeviceId() }

    var homeId by longPreference("homeId", 0L)

    var homeUid by longPreference("homeUid", 0L)

    private var quickList by stringPreference("quickList", "{\"quicks\":[]}")

    fun getQuicks() = gson.fromJson(quickList, Quicks::class.java).quicks

    fun putQuicks(quick: ArrayList<MiotBaseQuick>) = gson.toJson(Quicks(quick))

    data class Quicks(@SerializedName("quicks") val quicks: ArrayList<MiotBaseQuick>)
}