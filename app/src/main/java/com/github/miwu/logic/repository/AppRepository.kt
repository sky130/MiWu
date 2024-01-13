package com.github.miwu.logic.repository

import android.util.ArrayMap
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.preferences.AppPreferences
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotScenes

object AppRepository {
    private val job = Job()
    private val scope = CoroutineScope(job)
    val deviceList = ObservableArrayList<MiotDevices.Result.Device>()
    val homeList = ObservableArrayList<MiotHomes.Result.Home>()
    val sceneList = ObservableArrayList<MiotScenes.Result.Scene>()
    val homes = MutableLiveData<MiotHomes>()
    val devices = MutableLiveData<MiotDevices>()
    val scenes = MutableLiveData<MiotScenes>()

    private val deviceRoomName = ArrayMap<String, String>()

    fun getRoomName(device: MiotDevices.Result.Device): String {
        val name = deviceRoomName[device.did]
        if (name != null) return name
        for (home in homeList) {
            for (room in home.rooms) {
                if (device.did in room.dids) {
                    deviceRoomName[device.did] = room.name
                    return room.name
                }
            }
        }
        return "未知位置"
    }

    fun loadHomes() {
        scope.launch(Dispatchers.IO) {
            miot.getHomes().also {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        "加载家庭失败".toast()
                    } else {
                        homeList.clear()
                        it.result.homes?.let { it1 -> homeList.addAll(it1) }
                        it.result.shareHomes?.let { it1 -> homeList.addAll(it1) }
                        homes.value = it
                    }
                }
            }?.let {
                it.log.d()
                if (AppPreferences.homeId == 0L) {
                    it.result.homes?.firstNotNullOf { home ->
                        AppPreferences.homeId = home.id.toLong()
                        AppPreferences.homeUid = home.uid
                    }
                    loadDevice()
                }
            }
        }
    }

    fun loadDevice() {
        if (AppPreferences.homeId == 0L) return
        scope.launch(Dispatchers.IO) {
            miot.getDevices(AppPreferences.homeUid, AppPreferences.homeId)?.also {
                it.log.d()
            }.let {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        "加载设备失败".toast()
                    } else {
                        deviceList.clear()
                        it.result.deviceInfo?.let { it1 -> deviceList.addAll(it1) }
                        devices.value = it
                    }
                }
            }
        }
    }

    suspend fun loadDevices() {
        miot.getDevices(AppPreferences.homeUid, AppPreferences.homeId)?.also {
            it.log.d()
        }.let {
            withContext(Dispatchers.Main) {
                if (it == null) {
                    "加载设备失败".toast()
                } else {
                    deviceList.clear()
                    it.result.deviceInfo?.let { it1 -> deviceList.addAll(it1) }
                    devices.value = it
                }
            }
        }
    }
}