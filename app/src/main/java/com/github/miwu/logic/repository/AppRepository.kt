package com.github.miwu.logic.repository

import android.util.ArrayMap
import com.github.miwu.ktx.Logger
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kndroidx.KndroidX.scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.MiotClient
import miwu.miot.exception.MiotClientException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRepository : KoinComponent {

    @get:Synchronized
    private val roomMap = ArrayMap<String, String>()
    private val logger = Logger()

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
            _home.emit(runCatching {
                buildList {
                    val homes = miotClient.Home.getHomes().getOrThrow().result
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
                }
            }.toResultat())
        }
    }

    fun loadDevices() {
        scope.launch {
            device.emit(Resultat.Loading())
            device.emit(
                runCatching {
                    val homeUid = AppSetting.homeUid.value
                    val homeId = AppSetting.homeId.value
                    if (homeUid == 0L || homeId == 0L)
                        throw IllegalStateException("Invalid homeUid or homeId")
                    miotClient.Home.getDevices(homeUid, homeId)
                        .getOrThrow().result.deviceInfo ?: emptyList()
                }.toResultat()
            )

        }
    }

    fun loadScenes() {
        scope.launch {
            scene.emit(Resultat.Loading())
            scene.emit(
                runCatching {
                    val homeId = AppSetting.homeId.value
                    val ownerId = AppSetting.homeUid.value
                    miotClient.Home.getScenes(homeId, ownerId).getOrThrow().result.scenes
                        ?: emptyList()
                }.onFailure {
                    logger.error("load scene failure, {}", it.cause?.message)
                }.toResultat()
            )
        }
    }

    fun getRoomName(device: MiotDevice) = roomMap[device.did] ?: "未知位置"

    @Suppress("FunctionName")
    private fun <T> MutableResultListStateFlow(value: Resultat<List<T>>) = MutableStateFlow(value)

}