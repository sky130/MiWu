package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotHomeClient
import com.github.miwu.utils.MiotUserClient
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotClientException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRepositoryImpl : KoinComponent, AppRepository {
    private val scope: CoroutineScope by inject()
    private val deviceRepository: DeviceRepository by inject()
    private val dataStore: MiotUserDataStore by inject()
    private val logger = Logger()
    private var miotUserClient: MiotUserClient? = null
    private var miotHomeClient: MiotHomeClient? = null
    private var currentHomeId
        get() = AppSetting.homeId.value
        set(value) {
            AppSetting.homeId.value = value
        }
    private var currentOwnerId
        get() = AppSetting.ownerId.value
        set(value) {
            AppSetting.ownerId.value = value
        }
    private var currentUser: MiotUser? = null
        set(value) {
            field = value
            miotUserClient = value?.let { MiotUserClient(it) }
            miotHomeClient = value?.let { MiotHomeClient(it) }
        }
    override val miotUser: MiotUser? get() = currentUser
    override val homes = MutableResultListStateFlow<MiotHome>(Resultat.Loading())
    override val devices = MutableResultListStateFlow<MiotDevice>(Resultat.Loading())
    override val scenes = MutableResultListStateFlow<MiotScene>(Resultat.Loading())

    init {
        dataStore.data.onEach {
            currentUser = it
            refreshAll()
        }.launchIn(scope)
    }

    override fun refreshAll() {
        refreshHomes()
        refreshDevices()
        refreshScenes()
    }

    override fun refreshHomes() = scope.launch {
        homes.emit(Resultat.Loading())
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
        }.let { homes.emit(it.toResultat()) }
    }

    override fun refreshDevices() = scope.launch {
        devices.emit(Resultat.Loading())
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
        }.let { devices.emit(it.toResultat()) }
    }

    override fun refreshScenes() = scope.launch {
        scenes.emit(Resultat.Loading())
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
        }.let { scenes.emit(it.toResultat()) }
    }

    @Throws(IllegalStateException::class)
    fun getHomeDetails(): Result<Pair<Long, Long>> = runCatching {
        val homeId = currentHomeId
        val ownerUid = currentOwnerId
        if (ownerUid == 0L || homeId == 0L)
            throw IllegalStateException("Invalid ownerUid or homeId")
        homeId to ownerUid
    }

    override suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene) =
        runCatching {
            miotHomeClient
                ?.runScene(homeId, ownerUid, scene)
                ?.getOrThrow()
                ?: throw MiotClientException("MiotHomeClient is null")
        }

    override suspend fun getUserInfo() = runCatching {
        miotUserClient
            ?.getUserInfo()
            ?.getOrThrow()
            ?: throw IllegalStateException("MiotUserClient is null")
    }

    override fun setActiveHome(home: MiotHome) {
        currentHomeId = home.id.toLong()
        currentOwnerId = home.uid
        refreshDevices()
        refreshScenes()
    }

    @Suppress("FunctionName")
    private fun <T> MutableResultListStateFlow(value: Resultat<List<T>>) = MutableStateFlow(value)
}