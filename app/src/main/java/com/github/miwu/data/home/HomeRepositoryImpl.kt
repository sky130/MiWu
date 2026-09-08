package com.github.miwu.data.home

import com.github.miwu.domain.model.HomeData
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.repository.ResultState
import com.github.miwu.domain.repository.SettingsRepository
import com.github.miwu.utils.Logger
import com.github.miwu.utils.runCatchingSuspend
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.UserInfo
import org.koin.core.annotation.Named

class HomeRepositoryImpl(
    private val accountRepository: AccountRepository,
    private val metadataRepository: DeviceMetadataRepository,
    private val settingsRepository: SettingsRepository,
    private val homeDataLoader: HomeDataLoader,
    private val clientFactory: MiotClientFactory,
    @Named("app_scope") applicationScope: CoroutineScope,
) : HomeRepository {
    private val logger = Logger()
    private var homeClient: MiotHomeClient? = null
    private val cachedHomes = mutableMapOf<String, HomeData>()

    private val mutableHomes = MutableStateFlow<Resultat<List<MiotHome>>>(Resultat.loading())
    override val homes: ResultState<List<MiotHome>> = mutableHomes.asStateFlow()

    private val mutableCurrentHome = MutableStateFlow<Resultat<HomeData>>(Resultat.loading())
    override val currentHome: ResultState<HomeData> = mutableCurrentHome.asStateFlow()

    private val mutableUserInfo = MutableStateFlow(EMPTY_USER_INFO)
    override val userInfo = mutableUserInfo.asStateFlow()

    init {
        accountRepository.loginState
            .onEach(::onLoginStateChanged)
            .launchIn(applicationScope)
    }

    override suspend fun selectHome(home: MiotHome): Result<Unit> {
        mutableCurrentHome.emit(Resultat.loading())
        val result = runCatchingSuspend {
            settingsRepository.selectedHomeId = home.id.toLong()
            settingsRepository.selectedOwnerId = home.uid
            cachedHomes[home.id] ?: error("Home ${home.id} is not loaded")
        }
        result.onFailure { logger.error("select home failure, {}", it.stackTraceToString()) }
        mutableCurrentHome.emit(result.toResultat())
        return result.map { Unit }
    }

    override suspend fun runScene(scene: MiotScene): Result<Unit> = runCatchingSuspend {
        val client = homeClient ?: error("MiotHomeClient is not initialized")
        val home = homeDataLoader.getSceneHome(scene) ?: error("Scene home is not loaded")
        client.runScene(home, scene).getOrThrow()
        Unit
    }.onFailure {
        logger.error("run scene failure, {}", it.stackTraceToString())
    }

    override suspend fun refreshHomes() {
        mutableHomes.emit(Resultat.loading())
        val result = runCatchingSuspend {
            val client = homeClient ?: error("MiotHomeClient is not initialized")
            val response = client.getHomes().getOrThrow().result
            val homeList = response.homes + response.shareHomes.orEmpty()
            cachedHomes.clear()
            cachedHomes.putAll(homeDataLoader.load(client, homeList))
            homeList
        }

        result.onFailure {
            logger.error("refresh homes failure, {}", it.stackTraceToString())
        }
        mutableHomes.emit(result.toResultat())

        result.getOrNull()?.let { homeList ->
            val selectedHome = homeList.firstOrNull {
                it.id == settingsRepository.selectedHomeId.toString()
            } ?: homeList.firstOrNull()
            selectedHome?.let { selectHome(it) }
        }
    }

    override suspend fun refreshCurrentHome() {
        mutableCurrentHome.emit(Resultat.loading())
        val result = runCatchingSuspend {
            val client = homeClient ?: error("MiotHomeClient is not initialized")
            val homeId = settingsRepository.selectedHomeId.takeIf { it != 0L }
                ?: error("No active home")
            val home = cachedHomes[homeId.toString()]?.home
                ?: error("Active home is not loaded")
            homeDataLoader.load(client, home) ?: error("Failed to load active home")
        }
        result.onFailure {
            logger.error("refresh current home failure, {}", it.stackTraceToString())
        }.onSuccess { homeData ->
            cachedHomes[homeData.home.id] = homeData
        }
        mutableCurrentHome.emit(result.toResultat())
    }

    private suspend fun onLoginStateChanged(state: LoginState) {
        when (state) {
            LoginState.Success -> {
                val user = accountRepository.currentUser ?: return
                homeClient = clientFactory.createHomeClient(user)
                refreshUserInfo(user)
                refreshHomes()
            }

            LoginState.LoggedOut, is LoginState.Failure -> clearAccountState()
            LoginState.Loading, is LoginState.NetworkError -> Unit
        }
    }

    private suspend fun refreshUserInfo(user: miwu.miot.model.MiotUser) {
        runCatchingSuspend { clientFactory.createUserClient(user).getUserInfo().getOrThrow().result }
            .onSuccess { mutableUserInfo.emit(it) }
            .onFailure {
                logger.error("get user info failed, {}", it.message)
                mutableUserInfo.emit(EMPTY_USER_INFO)
            }
    }

    private suspend fun clearAccountState() {
        homeClient = null
        cachedHomes.clear()
        homeDataLoader.clear()
        settingsRepository.selectedHomeId = 0L
        settingsRepository.selectedOwnerId = 0L
        metadataRepository.clear()
        mutableHomes.emit(Resultat.loading())
        mutableCurrentHome.emit(Resultat.loading())
        mutableUserInfo.emit(EMPTY_USER_INFO)
    }

    private companion object {
        val EMPTY_USER_INFO = UserInfo(-1, "", "")
    }
}
