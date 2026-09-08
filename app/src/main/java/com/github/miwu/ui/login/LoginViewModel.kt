package com.github.miwu.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.usecase.account.LoginUseCase
import com.github.miwu.utils.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import miwu.miot.model.MiotUser
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val logger = Logger()
    private val mutableUiState = MutableStateFlow(LoginUiState())
    private val mutableEvent = MutableStateFlow<Event?>(null)
    private val mutableUser = MutableStateFlow("")
    private val mutablePassword = MutableStateFlow("")
    private var loginJob: Job? = null

    val user = mutableUser.asStateFlow()
    val password = mutablePassword.asStateFlow()
    val uiState = mutableUiState.asStateFlow()
    val event: StateFlow<Event?> = mutableEvent.asStateFlow()

    fun onUserChanged(value: CharSequence?) {
        mutableUser.value = value?.toString().orEmpty()
    }

    fun onPasswordChanged(value: CharSequence?) {
        mutablePassword.value = value?.toString().orEmpty()
    }

    fun requestClassicLogin() {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true)
            try {
                val loggedInUser = loginUseCase.loginByPassword(user.value, password.value).getOrThrow()
                event(Event.LoginSuccess(loggedInUser))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                loginFailure(e)
            } finally {
                mutableUiState.value = mutableUiState.value.copy(isLoading = false)
            }
        }
    }

    fun requestQRCodeLogin() {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            while (true) {
                currentCoroutineContext().ensureActive()
                try {
                    mutableUiState.value = LoginUiState()
                    val qrCodeData = loginUseCase.generateQrCode().getOrThrow()
                    mutableUiState.value = LoginUiState(qrCode = qrCodeData.data)
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

    private fun loginFailure(e: Throwable) {
        logger.warn("Login failed: {}", e.message)
        event(Event.LoginFailure(e))
    }

    fun cancelLogin() {
        loginJob?.cancel()
        loginJob = null
        mutableUiState.value = LoginUiState()
    }

    fun consumeEvent(event: Event) {
        mutableEvent.compareAndSet(event, null)
    }

    override fun onCleared() {
        loginJob?.cancel()
        super.onCleared()
    }

    private fun event(event: Event) {
        mutableEvent.value = event
    }

    data class LoginUiState(
        val isLoading: Boolean = false,
        val qrCode: String = "",
    )

    sealed interface Event {
        data class LoginSuccess(val user: MiotUser) : Event
        data class LoginFailure(val e: Throwable) : Event
    }
}
