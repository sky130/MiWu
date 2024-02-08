package com.github.miwu.logic.repository

import android.util.ArrayMap
import com.github.miwu.MainApplication
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.model.SmartHome
import com.github.miwu.miot.getSpecAttByAnnotation
import kndroidx.KndroidX.context
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.MiotManager
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotScenes
import miot.kotlin.utils.parseUrn
import java.io.File

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
    private val _smartFlow = MutableStateFlow<List<SmartHome>>(emptyList())
    private val _deviceRefreshFlow = MutableSharedFlow<Unit>()
    private val _sceneRefreshFlow = MutableSharedFlow<Unit>()
    private val _smartRefreshFlow = MutableSharedFlow<Unit>()
    val smartFlow get() = _smartFlow
    val smartRefreshFlow: Flow<Unit> get() = _smartRefreshFlow
    val sceneRefreshFlow: Flow<Unit> get() = _sceneRefreshFlow
    val deviceRefreshFlow: Flow<Unit> get() = _deviceRefreshFlow
    val sceneFlow: Flow<SceneList> get() = _sceneFlow
    val homeFlow: Flow<HomeList> get() = _homeFlow
    val deviceFlow: Flow<DeviceList> get() = _deviceFlow

    fun loadSmart() {
        scope.launch(Dispatchers.IO) {
            val room = ArrayMap<String, DeviceArrayList>()
            deviceFlow.take(1).collect { deviceList ->
                homeFlow.take(1).collect { homeList ->

                    for (i in homeList) {
                        if (i.id == AppPreferences.homeId.toString()) {
                            i.rooms.forEach {
                                if (it.dids.isNotEmpty()) room[it.name] = arrayListOf()
                            }
                            break
                        }
                    }

                    for (i in deviceList) {
                        room[getRoomName(i)]?.add(i)
                    }

                    val list = ArrayList<SmartHome>()

                    val jobs: ArrayList<Deferred<*>> = arrayListOf()

                    for ((i, devList) in room) {
                        SmartHome(i).apply {
                            devList.forEach { device ->
                                if (device.specType == null) return@forEach
                                getSpecAttByAnnotation(
                                    device,
                                    device.specType!!.parseUrn().name,
                                    getDeviceSpecAtt(device.specType!!) ?: return@forEach
                                )?.let {
                                    if (!it.isTextQuick) return@forEach
                                    this.deviceList.add(device)
                                    textList.clear()
                                    jobs.add(async {
                                        textList.addAll(
                                            it.getTextQuick()?.getTexts() ?: return@async
                                        )
                                        textList.log.d()
                                    })
                                }
                            }
                            list.add(this)
                        }
                    }

                    jobs.awaitAll()
                    _smartFlow.emit(list.filter { it.textList.isNotEmpty() })
                    _smartRefreshFlow.emit(Unit)
                }
            }
        }
    }


    @get:Synchronized
    private val roomMap = ArrayMap<String, String>()

    fun updateHome() {
        scope.launch {
            miot.getHomes().also {
                if (it == null) {
                    withContext(Dispatchers.Main) {
                        "加载家庭失败".toast()
                    }
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
        scope.launch {
            miot.getScenes(AppPreferences.homeId).let {
                if (it == null) {
                    withContext(Dispatchers.Main) {
                        "加载情景失败".toast()
                    }
                    _sceneFlow.emit(emptyList())
                } else {
                    it.result.scenes.let { scenes ->
                        if (scenes == null) {
                            _sceneFlow.emit(emptyList())
                        } else {
                            _sceneFlow.emit(scenes)
                        }
                    }
                }
                _sceneRefreshFlow.emit(Unit)
            }
        }
    }

    fun updateDevice() {
        if (AppPreferences.homeId == 0L) return
        scope.launch {
            miot.getDevices(AppPreferences.homeUid, AppPreferences.homeId).let { it ->
                if (it == null) {
                    withContext(Dispatchers.Main) {
                        "加载设备失败".toast()
                    }
                    _deviceFlow.emit(emptyList())
                } else {
                    it.result.deviceInfo.let { device ->
                        if (device == null) {
                            _deviceFlow.emit(emptyList())
                        } else {
                            val list = ArrayList(device)
                            list.sortBy { item -> !item.isOnline }
                            _deviceFlow.emit(list)
                        }
                    }
                }
                _deviceRefreshFlow.emit(Unit)
            }
        }
    }

    fun getRoomName(device: MiwuDevice): String {
        return roomMap[device.did] ?: "未知位置"
    }

    fun getRoomName(device: MiotDevices.Result.Device): String {
        return roomMap[device.did] ?: "未知位置"
    }

    suspend fun getDeviceSpecAtt(urn: String) = withContext(Dispatchers.IO) {
        File(context.cacheDir.absolutePath + "/" + urn.hashCode()).let { file ->
            if (file.isFile) {
                val att = MainApplication.gson.fromJson(file.readText(), SpecAtt::class.java)
                return@withContext att
            } else {
                val att = MiotManager.getSpecAttWithLanguage(urn)
                att?.let { at ->
                    file.writeText(MainApplication.gson.toJson(at))
                    return@withContext att
                }
            }
        }
    }
}