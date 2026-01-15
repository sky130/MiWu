package com.github.miwu.logic.repository

import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotHomeClient
import com.github.miwu.utils.MiotUserClient
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotClientException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRepository : KoinComponent {
    private val scope: CoroutineScope by inject()
    private val deviceRepository: DeviceRepository by inject()
    private val logger = Logger()

    private val _homes = MutableResultListStateFlow<MiotHome>(Resultat.Loading())
    val homes = _homes.asStateFlow()

    private val _devices = MutableResultListStateFlow<MiotDevice>(Resultat.Loading())
    val devices = _devices.asStateFlow()

    private val _scenes = MutableResultListStateFlow<MiotScene>(Resultat.Loading())
    val scenes = _scenes.asStateFlow()

    private var miotUserClient: MiotUserClient? = null
    private var miotHomeClient: MiotHomeClient? = null
    private var currentHomeId
        get() = AppSetting.homeId.value
        set(value) {
            AppSetting.homeId.value = value
        }
    private var currentOwnerId
        get() = AppSetting.homeUid.value
        set(value) {
            AppSetting.homeUid.value = value
        }

    var miotUser: MiotUser? = null
        set(value) {
            field = value
            if (value == null) return
            miotUserClient = MiotUserClient(value)
            miotHomeClient = MiotHomeClient(value)
        }

    fun loadAll() {
        loadHomes()
        loadDevices()
        loadScenes()
    }

    fun loadHomes() = scope.launch {
        _homes.emit(Resultat.Loading())
        runCatching {
            val homes = miotHomeClient
                ?.getHomes()
                ?.getOrThrow()
                ?.result
                ?: throw MiotClientException("MiotHomeClient is null")
            buildList {
                addAll(homes.homes)
                homes.shareHomes?.let { addAll(it) }
            }
        }.onSuccess { list ->
            list.flatMap(MiotHome::rooms)
                .flatMap { room -> room.dids.map { it to room.name } }
                .let { deviceRepository.addRoom(it) }
            list.takeIf { currentHomeId == 0L }
                ?.firstOrNull()
                ?.let(::setActiveHome)
        }.onFailure {
            logger.error("load home failure, {}", it.stackTraceToString())
        }.let { _homes.emit(it.toResultat()) }
    }

    fun loadDevices() = scope.launch {
        _devices.emit(Resultat.Loading())
        runCatching {
            val (homeId, ownerId) = getHomeDetails().getOrThrow()
            miotHomeClient
                ?.getDevices(homeId, ownerId)
                ?.getOrThrow()
                ?.result
                ?.deviceInfo
                ?: emptyList()
                ?: throw MiotClientException("MiotHomeClient is null")
        }.onSuccess {
            deviceRepository.addIcon(it.map(MiotDevice::model))
        }.onFailure {
            logger.error("load device failure, {}", it.stackTraceToString())
        }.let { _devices.emit(it.toResultat()) }
    }

    fun loadScenes() = scope.launch {
        _scenes.emit(Resultat.Loading())
        runCatching {
            val (homeId, ownerUid) = getHomeDetails().getOrThrow()
            miotHomeClient
                ?.getScenes(homeId, ownerUid)
                ?.getOrThrow()
                ?.result
                ?.scenes
                ?: emptyList()
                ?: throw MiotClientException("MiotHomeClient is null")
        }.onFailure {
            logger.error("load scene failure, {}", it.stackTraceToString())
        }.let { _scenes.emit(it.toResultat()) }
    }

    @Throws(IllegalStateException::class)
    fun getHomeDetails(): Result<Pair<Long, Long>> = runCatching {
        val homeId = currentHomeId
        val ownerUid = currentOwnerId
        if (ownerUid == 0L || homeId == 0L)
            throw IllegalStateException("Invalid ownerUid or homeId")
        homeId to ownerUid
    }

    suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene) = runCatching {
        miotHomeClient
            ?.runScene(homeId, ownerUid, scene)
            ?.getOrThrow()
            ?: throw MiotClientException("MiotHomeClient is null")
    }

    suspend fun getUserInfo() = runCatching {
        miotUserClient
            ?.getUserInfo()
            ?.getOrThrow()
            ?: throw IllegalStateException("MiotUserClient is null")
    }

    private fun setActiveHome(home: MiotHome) {
        currentHomeId = home.id.toLong()
        currentOwnerId = home.uid
        loadDevices()
        loadScenes()
    }

    @Suppress("FunctionName")
    private fun <T> MutableResultListStateFlow(value: Resultat<List<T>>) = MutableStateFlow(value)
}