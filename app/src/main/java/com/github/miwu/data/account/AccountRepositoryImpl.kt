package com.github.miwu.data.account

import com.github.miwu.data.account.local.MiotUserDataStore
import com.github.miwu.data.account.local.serializer.MiotUserSerializer
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.gateway.DeviceIdProvider
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.utils.Logger
import com.github.miwu.utils.throwIfCancelled
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import miwu.miot.exception.MiotAuthException
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.annotation.Named

@OptIn(ExperimentalSerializationApi::class)
class AccountRepositoryImpl(
    private val loginProvider: MiotLoginProvider,
    private val dataStore: MiotUserDataStore,
    private val deviceIdProvider: DeviceIdProvider,
    private val clientFactory: MiotClientFactory,
    @Named("app_scope") applicationScope: CoroutineScope,
) : AccountRepository {
    private val logger = Logger()

    @Volatile
    override var currentUser: MiotUser? = null
        private set(value) {
            field = value
        }

    private val mutableLoginState = MutableStateFlow<LoginState>(LoginState.Loading)
    override val loginState: StateFlow<LoginState> = mutableLoginState.asStateFlow()

    init {
        dataStore.data
            .onEach(::validateStoredUser)
            .catch { error ->
                if (error is CancellationException) throw error
                logger.error("observe user storage failure, {}", error.stackTraceToString())
                currentUser = null
                mutableLoginState.emit(LoginState.Failure(error.message ?: "unknown", error))
            }
            .launchIn(applicationScope)
    }

    override suspend fun saveUser(user: MiotUser): MiotUser {
        val storedUser = user.copy(deviceId = deviceIdProvider.id)
        dataStore.updateData { storedUser }
        currentUser = storedUser
        mutableLoginState.emit(LoginState.Success)
        return storedUser
    }

    override suspend fun logout() {
        dataStore.updateData { MiotUserSerializer.defaultValue }
    }

    private suspend fun validateStoredUser(storedUser: MiotUser) {
        mutableLoginState.emit(LoginState.Loading)
        val user = storedUser.copy(deviceId = deviceIdProvider.id)
        if (!user.hasCredentials()) {
            currentUser = null
            mutableLoginState.emit(LoginState.LoggedOut)
            return
        }

        currentUser = user
        val tokenValid = clientFactory.createUserClient(user)
            .getIsServiceTokenValid()
            .throwIfCancelled()
            .getOrNull()
            ?: false
        if (tokenValid) {
            mutableLoginState.emit(LoginState.Success)
        } else {
            refreshToken(user)
        }
    }

    private suspend fun refreshToken(user: MiotUser) {
        loginProvider.refreshServiceToken(user)
            .onSuccess { refreshedUser -> saveUser(refreshedUser) }
            .onFailure { error ->
                if (error is CancellationException) throw error
                logger.error("refresh user token failure, {}", error.stackTraceToString())
                mutableLoginState.emit(error.toLoginState())
            }
    }

    private fun Throwable.toLoginState(): LoginState =
        if (this is MiotAuthException || this is MissingFieldException) {
            LoginState.Failure(message ?: "unknown", this)
        } else {
            LoginState.NetworkError(message ?: "unknown")
        }

    private fun MiotUser.hasCredentials(): Boolean =
        userId.isNotEmpty() && cUserId.isNotEmpty() && passToken.isNotEmpty()
}
