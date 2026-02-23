package com.github.miwu.logic.repository.impl

import androidx.lifecycle.asLiveData
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.logic.state.LoginState
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotHomeClient
import com.github.miwu.utils.MiotUserClient
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotClientException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo
import miwu.miot.model.miot.MiotUserInfo.UserInfo
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRepositoryImpl : KoinComponent, AppRepository {
    private val logger = Logger()
    private val scope: CoroutineScope by inject()
    private val deviceRepository: DeviceRepository by inject()
    private val dataStore: MiotUserDataStore by inject()
    private val loginProvider: MiotLoginProvider by inject()
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
    override val loginStatus = MutableStateFlow<LoginState>(LoginState.Loading)
    override val userInfo = MutableStateFlow(UserInfo(0L, "", "null"))

    //    val info = flow {
//        runCatching {
//            appRepository.getUserInfo().getOrThrow()
//        }.onSuccess {
//            emit(it.info)
//        }.onFailure {
//            it.message?.toast()
//            logger.error("get user info failed, {}", it.message)
//            it.printStackTrace()
//            emit(MiotUserInfo.UserInfo(0L, "", "null"))
//        }
//    }.asLiveData()
    init {
        dataStore.data.onEach {
            currentUser = it
            loginStatus.emit(LoginState.Loading)
            val isTokenValid = miotUserClient
                ?.getIsServiceTokenValid()
                ?.getOrDefault(false)
                ?: false
            if (!isTokenValid) {
                loginProvider.refreshServiceToken(it)
                    .onSuccess { newUser -> dataStore.updateData { newUser } }
                    .onFailure { e ->
                        if (e is MiotAuthException) {
                            // 这里登录信息彻底过期, 需要退出应用再登录
                            loginStatus.emit(LoginState.Failure(e.message ?: "unknown", e))
                        } else {
                            loginStatus.emit(LoginState.NetworkError(e.message ?: "unknown"))
                        }
                    }
            } else {
                loginStatus.emit(LoginState.Success)
                refreshAll()
            }
        }.launchIn(scope)
    }

    override fun refreshAll() {
        refreshHomes()
        refreshDevices()
        refreshScenes()
    }

    override fun refreshUserInfo() = scope.launch {
        runCatching {
            getUserInfo().getOrThrow()
        }.onSuccess {
            userInfo.emit(it.info)
        }.onFailure {
            logger.error("get user info failed, {}", it.message)
            userInfo.emit(UserInfo(0L, "", "null"))
        }
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
            miotUser?.let { loginProvider.refreshServiceToken(it) }
            miotHomeClient
                ?.getDevices(homeId, ownerId)
                ?.getOrThrow()
                ?.result
                ?.deviceInfo
                ?: emptyList()
                ?: throw MiotClientException("MiotHomeClient is null")
        }.onSuccess { devices ->
            homes.takeWhile { it.isSuccess }
                .let { it.mapNotNull { it }

                }
            deviceRepository.addIcon(devices.map(MiotDevice::model))
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