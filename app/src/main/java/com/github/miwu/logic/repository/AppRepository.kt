package com.github.miwu.logic.repository

import android.util.ArrayMap
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import kndroidx.KndroidX.scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.MiotClient
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRepository : KoinComponent {

    @get:Synchronized
    private val roomMap = ArrayMap<String, String>()

    private val _home = MutableResultListStateFlow<MiotHome>(Resultat.Loading())
    val homes = _home.asStateFlow()

    private val device = MutableResultListStateFlow<MiotDevice>(Resultat.Loading())
    val devices = device.asStateFlow()

    private val scene = MutableResultListStateFlow<MiotScene>(Resultat.Loading())
    val scenes = scene.asStateFlow()

    lateinit var miotUser: MiotUser
    val miotClient: MiotClient by inject()

    fun loadAll() {
        miotClient.setUser(miotUser)
        loadHomes()
        loadDevices()
        loadScenes()
    }

    fun loadHomes() {
        scope.launch {
            _home.emit(Resultat.Loading())
            runCatching {
                _home.emit(buildList<MiotHome> {
                    val homes = miotClient.Home.getHomes().result
                    addAll(homes.homes)
                    homes.shareHomes?.let { addAll(it) }
                    forEach { home ->
                        home.rooms.forEach { room ->
                            room.dids.forEach { did ->
                                roomMap[did] = room.name
                            }
                        }
                    }
                    if (AppSetting.homeId.value != 0L) return@buildList
                    withContext(Dispatchers.Main) {
                        firstNotNullOf { home ->
                            AppSetting.homeId.value = home.id.toLong()
                            AppSetting.homeUid.value = home.uid
                            loadDevices()
                            loadScenes()
                        }
                    }
                }.let { Resultat.Success(it) })
            }.onFailure {
                _home.emit(Resultat.Failure(it))
            }
        }
    }

    fun loadDevices() {
        scope.launch {
            device.emit(Resultat.Loading())
            runCatching {
                device.emit(
                    buildList {
                        val homeUid = AppSetting.homeUid.value.takeIf { it != 0L }
                            ?: throw IllegalStateException()
                        val homeId = AppSetting.homeId.value.takeIf { it != 0L }
                            ?: throw IllegalStateException()
                        val devices = miotClient.Home.getDevices(homeUid, homeId).result.deviceInfo
                            ?: throw IllegalStateException()
                        addAll(devices)
                    }.let { Resultat.Success(it) }
                )
            }.onFailure {
                device.emit(Resultat.Failure(it))
            }
        }
    }

    fun loadScenes() {
        scope.launch {
            scene.emit(Resultat.Loading())
            runCatching {
                scene.emit(
                    buildList {
                        val homeId = AppSetting.homeId.value.takeIf { it != 0L }
                            ?: throw IllegalStateException()
                        val scenes = miotClient.Home.getScenes(homeId).result.scenes
                            ?: throw IllegalStateException()
                        addAll(scenes)
                    }.let { Resultat.Success(it) }
                )
            }.onFailure {
                scene.emit(Resultat.Failure(it))
            }
        }
    }

    fun getRoomName(device: MiotDevice) = roomMap[device.did] ?: "未知位置"


    private fun <T> MutableResultListStateFlow(value: Resultat<List<T>>) = MutableStateFlow(value)

}