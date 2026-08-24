package com.github.miwu.logic.auth

import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import com.github.miwu.logic.state.LoginState
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotUserClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import miwu.miot.exception.MiotAuthException
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import miwu.miot.client.MiotUserClient
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalSerializationApi::class)
class AuthService(
    private val loginProvider: MiotLoginProvider,
    private val dataStore: MiotUserDataStore,
    private val scope: CoroutineScope,
): KoinComponent {
    private val logger = Logger()
    private var currentUser: MiotUser? = null
        set(value) {
            field = value
            miotUserClient = value?.let { MiotUserClient(it) }
        }
    private var miotUserClient: MiotUserClient? = null

    private val _loginStatus = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginStatus: StateFlow<LoginState> = _loginStatus

    init {
        dataStore.data.onEach { user ->
            currentUser = user
            _loginStatus.emit(LoginState.Loading)
            validateAndUpdateLoginStatus(user)
        }.launchIn(scope)
    }

    private suspend fun validateAndUpdateLoginStatus(user: MiotUser) {
        if (!user.isLogin()) {
            currentUser = null
            _loginStatus.emit(LoginState.LoggedOut)
            return
        }
        val isTokenValid = miotUserClient
            ?.takeIf { user.isLogin() }
            ?.getIsServiceTokenValid()
            ?.getOrNull()
            ?: false

        if (!isTokenValid) {
            refreshToken(user)
        } else {
            _loginStatus.emit(LoginState.Success)
        }
    }

    private suspend fun refreshToken(user: MiotUser) {
        loginProvider.refreshServiceToken(user)
            .onSuccess { newUser -> dataStore.updateData { newUser } }
            .onFailure { e ->
                if (e is CancellationException) throw e
                logger.error("refresh user token failure, {}", e.stackTraceToString())
                handleTokenRefreshFailure(e)
            }
    }

    private suspend fun handleTokenRefreshFailure(e: Throwable) {
        if (e is MiotAuthException || e is MissingFieldException) {
            _loginStatus.emit(LoginState.Failure(e.message ?: "unknown", e))
        } else {
            _loginStatus.emit(LoginState.NetworkError(e.message ?: "unknown"))
        }
    }

    fun getCurrentUser(): MiotUser? = currentUser

    fun getMiotUserClient() = miotUserClient

    suspend fun logout() {
        dataStore.updateData { MiotUserSerializer.defaultValue }
        currentUser = null
        _loginStatus.emit(LoginState.LoggedOut)
    }
}
