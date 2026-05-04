package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.auth.AuthService
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.logic.state.LoginState
import com.github.miwu.logic.usecase.home.ConvertHomeDataUseCase
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotHomeClient
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.MiotResponse
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.UserInfo
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalSerializationApi::class)
class MiotRepositoryImpl(
    private val authService: AuthService,
    private val cacheRepository: CacheRepository,
    private val convertHomeData: ConvertHomeDataUseCase,
    private val scope: CoroutineScope,
) : MiotRepository, KoinComponent {
    private val logger = Logger()
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
    override val loginStatus: StateFlow<LoginState> = authService.loginStatus
    override val homes = MutableResultatState<List<MiotHome>>(Resultat.loading())
    override val currentHome = MutableResultatState<MiotHomeData>(Resultat.loading())
    override val user get() = authService.getCurrentUser()
    override val userInfo = MutableStateFlow(UserInfo(-1, "", ""))

    init {
        authService.loginStatus.onEach { state ->
            if (state is LoginState.Success) {
                miotHomeClient = authService.getCurrentUser()?.let { MiotHomeClient(it) }
                refreshUserInfo()
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
                convertHomeData.getSceneHome(scene)?.let { miotHomeClient?.runScene(it, scene) }
            }.onFailure {
                logger.error("run scene failure, {}", it.stackTraceToString())
            }
        }
    }

    override fun refreshHomes() {
        scope.launch {
            runCatching {
                homes.emit(Resultat.loading())
                val client = miotHomeClient ?: error("MiotHomeClient is not initialized")
                val homesList = client
                    .getHomes()
                    .getOrThrow()
                    .result
                    .let { it.homes + it.shareHomes.orEmpty() }
                client to homesList
            }.onFailure {
                logger.error("refresh home failure, {}", it.stackTraceToString())
                homes.emit(Resultat.failure(it))
            }.onSuccess { (client, homesList) ->
                cacheHome.putAll(convertHomeData(client, homesList))
                val home = homesList.takeIf { currentHomeId == 0L }
                    ?.firstOrNull()
                    ?: homesList.firstOrNull { it.id == currentHomeId.toString() }
                home?.let { setActiveHome(it) }
            }.let { homes.emit(it.map { it.second }.toResultat()) }
        }
    }

    override fun refreshCurrentHome() {
        scope.launch {
            currentHome.emit(Resultat.loading())
            runCatching {
                val client = miotHomeClient ?: error("MiotHomeClient is not initialized")
                val currentHomeId = currentHomeId.toString()
                    .takeIf(String::isNotEmpty)
                    ?: error("No active home")
                val home = cacheHome[currentHomeId]?.home
                    ?: error("MiotHome not found in cache")
                convertHomeData(client, home) ?: error("Failed to convert MiotHome to MiotHomeData")
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
            userInfo.emit(it.result)
        }.onFailure {
            logger.error("get user info failed, {}", it.message)
            userInfo.emit(UserInfo(0L, "", ""))
        }
    }

    private suspend fun getUserInfo() = runCatching {
        authService.getMiotUserClient()
            ?.getUserInfo()
            ?.getOrThrow()
            ?: throw IllegalStateException("MiotUserClient is null")
    }

    @Suppress("FunctionName")
    inline fun <reified T> MutableResultatState(defaultValue: Resultat<T>) =
        MutableStateFlow<Resultat<T>>(defaultValue)
}