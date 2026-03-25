package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.logic.state.LoginState
import com.github.miwu.utils.Logger
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.github.miwu.utils.MiotHomeClient
import com.github.miwu.utils.MiotUserClient
import com.github.miwu.utils.MiotDeviceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotAuthException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotHomes.Result.Home.Room
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo
import miwu.miot.model.miot.MiotUserInfo.UserInfo
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalSerializationApi::class)
class MiotRepositoryImpl() : MiotRepository, KoinComponent {
    private val logger = Logger()
    private val sceneMutex = Mutex()
    private val sceneMap = mutableMapOf<MiotScene, MiotHome>()
    private val loginProvider: MiotLoginProvider by inject()
    private val cacheRepository: CacheRepository by inject()
    private val dataStore: MiotUserDataStore by inject()
    private val scope: CoroutineScope by inject()
    private var currentUser: MiotUser? = null
        set(value) {
            field = value
            miotUserClient = value?.let { MiotUserClient(it) }
            miotHomeClient = value?.let { MiotHomeClient(it) }
        }
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
    private val cacheHome = mutableMapOf<String, MiotHomeData>()
    override val loginStatus = MutableStateFlow<LoginState>(LoginState.Loading)
    override val homes = MutableResultatState<List<MiotHome>>(Resultat.loading())
    override val currentHome = MutableResultatState<MiotHomeData>(Resultat.loading())
    override val user get() = currentUser
    override val userInfo = MutableStateFlow(MiotUserInfo.UserInfo(-1, "", ""))

    init {
        dataStore.data.onEach { user ->
            currentUser = user
            loginStatus.emit(LoginState.Loading)
            val isTokenValid = miotUserClient
                ?.takeIf { user.isLogin() }
                ?.getIsServiceTokenValid()
                ?.getOrNull()
                ?: false
            if (!isTokenValid) {
                loginProvider.refreshServiceToken(user)
                    .onSuccess { newUser -> dataStore.updateData { newUser } }
                    .onFailure { e ->
                        e.printStackTrace()
                        logger.error("refresh user token failure, {}", e.stackTraceToString())
                        if (e is MiotAuthException || e is MissingFieldException) {
                            // 这里登录信息彻底过期, 需要退出应用再登录
                            loginStatus.emit(LoginState.Failure(e.message ?: "unknown", e))
                        } else {
                            loginStatus.emit(LoginState.NetworkError(e.message ?: "unknown"))
                        }
                    }
            } else {
                refreshUserInfo()
                loginStatus.emit(LoginState.Success)
                refreshHomes()
            }
        }.launchIn(scope)
    }

    override fun setActiveHome(home: MiotHome) {
        scope.launch {
            currentHome.emit(Resultat.loading())
            runCatching {
                currentHomeId = home.id.toLong()
                currentOwnerId = home.uid
                cacheHome[home.id] ?: error("MiotHome not found")
            }.onFailure {
                logger.error("set active home failure, {}", it.stackTraceToString())
            }.let {
                currentHome.emit(it.toResultat())
            }
        }
    }

    override fun runScene(scene: MiotScene) {
        scope.launch {
            runCatching {
                sceneMap[scene]?.let { miotHomeClient?.runScene(it, scene) }
            }.onFailure {
                logger.error("run scene failure, {}", it.stackTraceToString())
            }
        }
    }

    override fun refreshHomes() {
        scope.launch {
            runCatching {
                homes.emit(Resultat.loading())
                miotHomeClient
                    ?.getHomes()
                    ?.getOrThrow()
                    ?.let(MiotHomes::result)
                    ?.let { it.homes + it.shareHomes.orEmpty() }
                    ?: error("MiotHomeClient is not initialized")
            }.onFailure {
                logger.error("refresh home failure, {}", it.stackTraceToString())
                homes.emit(Resultat.failure(it))
            }.onSuccess { homes ->
                cacheHome.putAll(convertToData(homes))
                val home = homes.takeIf { currentHomeId == 0L }
                    ?.firstOrNull()
                    ?: homes.firstOrNull { it.id == currentHomeId.toString() }
                home?.let { setActiveHome(it) }
            }.let { homes.emit(it.toResultat()) }
        }
    }

    private suspend fun convertToData(homes: List<MiotHome>) = withContext(Dispatchers.IO) {
        homes.map { home -> async { convertToData(home)?.let { home.id to it } } }
            .awaitAll()
            .filterNotNull()
    }

    private suspend fun convertToData(home: MiotHome) = miotHomeClient
        ?.let { client ->
            val devices = getDevices(client, home).sortedBy(MiotDevice::name)
            val deviceMap = devices.associateBy(MiotDevice::did)

            devices.associateBy(MiotDevice::did) { device ->
                home.rooms.firstOrNull { device.did in it.dids }?.name ?: "未知"
            }.let { cacheRepository.addRoom(it) }

            MiotHomeData(
                home = home,
                rooms = home.rooms.associateBy(Room::name) {
                    it.dids
                        .mapNotNull(deviceMap::get)
                        .sortedBy(MiotDevice::name)
                },
                scenes = client.getScenes(home)
                    .getOrNull()
                    ?.result
                    ?.scenes
                    ?.sortedBy(MiotScene::name)
                    .orEmpty().also { scenes ->
                        sceneMutex.withLock {
                            sceneMap += scenes.associateBy({ it }) { home }
                        }
                    },
                devices = devices,
                roomMap = home.rooms.associateBy(Room::name)
            )
        }

    private suspend fun getDevices(client: MiotHomeClient, home: MiotHome) =
        client.getDevices(home.id.toLong(), home.uid)
            .onFailure {
                logger.error("get devices of home failure, {}", it.stackTraceToString())
            }
            .getOrNull()
            ?.result
            ?.deviceInfo
            ?.also { cacheRepository.addIcon(it.map(MiotDevice::model)) }
            .orEmpty()

    override fun refreshCurrentHome() {
        scope.launch {
            currentHome.emit(Resultat.loading())
            runCatching {
                val currentHomeId = currentHomeId.toString()
                    .takeIf(String::isNotEmpty)
                    ?: error("No active home")
                val home = cacheHome[currentHomeId]?.home
                    ?: error("MiotHome not found in cache")
                convertToData(home) ?: error("Failed to convert MiotHome to MiotHomeData")
            }.onFailure {
                logger.error("refresh current home failure, {}", it.stackTraceToString())
            }.onSuccess { homeData ->
                cacheHome[homeData.home.id] = homeData
            }.let { currentHome.emit(it.toResultat()) }
        }
    }

    private fun refreshUserInfo() = scope.launch {
        runCatching {
            getUserInfo().getOrThrow()
        }.onSuccess {
            userInfo.emit(it.info)
        }.onFailure {
            logger.error("get user info failed, {}", it.message)
            userInfo.emit(UserInfo(0L, "", "null"))
        }
    }

    private suspend fun getUserInfo() = runCatching {
        miotUserClient
            ?.getUserInfo()
            ?.getOrThrow()
            ?: throw IllegalStateException("MiotUserClient is null")
    }

    @Suppress("FunctionName")
    inline fun <reified T> MutableResultatState(defaultValue: Resultat<T>) =
        MutableStateFlow<Resultat<T>>(defaultValue)
}