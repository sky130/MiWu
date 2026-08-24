package com.github.miwu.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.usecase.login.LoginUseCase
import com.github.miwu.utils.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.model.MiotUser
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    val miotRepository: MiotRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val logger = Logger()
    private val _uiState = MutableStateFlow(LoginUiState())
    private val _event = MutableSharedFlow<Event>()
    private var loginJob: Job? = null

    val user = MutableStateFlow("")
    val password = MutableStateFlow("")
    val uiState = _uiState.asStateFlow()
    val event = _event.asSharedFlow()

    fun requestClassicLogin() {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val loggedInUser = withContext(ioDispatcher) {
                    loginUseCase.loginByPassword(user.value, password.value).getOrThrow()
                }
                event(Event.LoginSuccess(loggedInUser))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                loginFailure(e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun requestQRCodeLogin() {
        loginJob?.cancel()
        loginJob = viewModelScope.launch(ioDispatcher) {
            while (true) {
                currentCoroutineContext().ensureActive()
                try {
                    _uiState.value = LoginUiState()
                    val qrCodeData = loginUseCase.generateQrCode().getOrThrow()
                    _uiState.value = LoginUiState(qrCode = qrCodeData.data)
                    val loggedInUser = loginUseCase.loginByQrCode(qrCodeData.loginUrl).getOrThrow()
                    event(Event.LoginSuccess(loggedInUser))
                    return@launch
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    if (e is SocketTimeoutException || e is TimeoutException) continue
                    loginFailure(e)
                    return@launch
                }
            }
        }
    }

    private suspend fun loginFailure(e: Throwable) {
        logger.warn("Login failed: {}", e.message)
        event(Event.LoginFailure(e))
    }

    fun cancelLogin() {
        loginJob?.cancel()
        loginJob = null
        _uiState.value = LoginUiState()
    }

    override fun onCleared() {
        loginJob?.cancel()
        super.onCleared()
    }

    private suspend fun event(event: Event) = _event.emit(event)

    data class LoginUiState(
        val isLoading: Boolean = false,
        val qrCode: String = "",
    )

    sealed interface Event {
        data class LoginSuccess(val user: MiotUser) : Event
        data class LoginFailure(val e: Throwable) : Event
    }
}
