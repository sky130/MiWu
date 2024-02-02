package com.github.miwu.logic.repository

import android.util.ArrayMap
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.preferences.AppPreferences
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotScenes

typealias DeviceList = List<MiotDevices.Result.Device>
typealias DeviceArrayList = ArrayList<MiotDevices.Result.Device>
typealias HomeList = List<MiotHomes.Result.Home>
typealias HomeArrayList = ArrayList<MiotHomes.Result.Home>
typealias SceneList = List<MiotScenes.Result.Scene>
typealias SceneArrayList = ArrayList<MiotScenes.Result.Scene>

object AppRepository {
    private val job = Job()
    private val scope = CoroutineScope(job)
    private val _deviceFlow = MutableStateFlow<DeviceList>(emptyList())
    private val _homeFlow = MutableStateFlow<HomeList>(emptyList())
    private val _sceneFlow = MutableStateFlow<SceneList>(emptyList())
    val sceneFlow: Flow<SceneList> get() = _sceneFlow
    val homeFlow: Flow<HomeList> get() = _homeFlow
    val deviceFlow: Flow<DeviceList> get() = _deviceFlow

    @get:Synchronized
    private val roomMap = ArrayMap<String, String>()

    fun updateHome() {
        scope.launch(Dispatchers.IO) {
            miot.getHomes().also {
                if (it == null) {
                    "加载家庭失败".toast()
                } else {
                    val list = HomeArrayList()
                    it.result.homes.let { home -> list.addAll(home) }
                    it.result.shareHomes?.let { home -> list.addAll(home) }
                    _homeFlow.emit(list)
                    for (home in list) {
                        for (room in home.rooms) {
                            for (did in room.dids) {
                                roomMap[did] = room.name
                            }
                        }
                    }
                }
            }?.let {
                if (AppPreferences.homeId == 0L) {
                    it.result.homes.firstNotNullOf { home ->
                        AppPreferences.homeId = home.id.toLong()
                        AppPreferences.homeUid = home.uid
                    }
                }
            }
        }
    }

    fun updateScene() {
        if (AppPreferences.homeId == 0L) return
        scope.launch(Dispatchers.IO) {
            miot.getScenes(AppPreferences.homeId)?.also {
                it.log.d()
            }.let {
                if (it == null) {
                    "加载设备失败".toast()
                } else {
                    it.result.scenes?.let { scenes -> _sceneFlow.emit(scenes) }
                }
            }
        }
    }

    fun updateDevice() {
        if (AppPreferences.homeId == 0L) return
        scope.launch(Dispatchers.IO) {
            miot.getDevices(AppPreferences.homeUid, AppPreferences.homeId).let {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        "加载设备失败".toast()
                    } else {
                        it.result.deviceInfo?.let { device ->
                            val list = ArrayList(device)
                            list.sortBy { !it.isOnline }
                            _deviceFlow.emit(list)
                        }
                    }
                }
            }
        }
    }

    fun getRoomName(device: MiwuDevice): String {
        return roomMap[device.did] ?: "未知位置"
    }

    fun getRoomName(device: MiotDevices.Result.Device): String {
        return roomMap[device.did] ?: "未知位置"
    }

}