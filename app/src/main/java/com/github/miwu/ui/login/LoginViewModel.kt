package com.github.miwu.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.usecase.login.LoginUseCase
import com.github.miwu.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import miwu.miot.model.MiotUser
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    val miotRepository: MiotRepository,
) : ViewModel() {
    private val logger = Logger()
    private val loginJob = Job()
    private val scope = CoroutineScope(loginJob)
    private val _qrcode = MutableStateFlow("")
    private val _event = MutableSharedFlow<Event>()

    val user = MutableStateFlow("")
    val password = MutableStateFlow("")

    val qrcode = _qrcode.asStateFlow()
    val event = _event.asSharedFlow()

    fun requestClassicLogin() {
        val user = user.value
        val pwd = password.value
        viewModelScope.launch(Dispatchers.IO) {
            event(Event.ShowLoading(true))
            loginUseCase.loginByPassword(user, pwd)
                .onFailure { e -> loginFailure(e) }
                .onSuccess { user ->
                    event(Event.LoginSuccess(user))
                    event(Event.ShowLoading(false))
                }
        }
    }

    fun requestQRCodeLogin() {
        logger.info("Request for a login qrcode")
        loginJob.cancelChildren()
        scope.launch(Dispatchers.IO) {
            runCatching {
                _qrcode.emit("")
                val qrCodeData = loginUseCase.generateQrCode().getOrThrow()
                _qrcode.emit(qrCodeData.data)
                loginUseCase.loginByQrCode(qrCodeData.loginUrl).getOrThrow()
            }.onFailure { e ->
                if (e is SocketTimeoutException || e is TimeoutException) {
                    requestQRCodeLogin()
                } else {
                    loginFailure(e)
                }
            }.onSuccess { user ->
                event(Event.LoginSuccess(user))
            }
        }
    }

    private suspend fun loginFailure(e: Throwable) {
        logger.warn("login failure, cause by {}", e.stackTraceToString())
        event(Event.LoginFailure(e))
    }

    fun cancelLogin() {
        loginJob.cancelChildren()
        _qrcode.value = ""
    }

    override fun onCleared() {
        loginJob.cancelChildren()
        super.onCleared()
    }

    private suspend fun event(event: Event) = _event.emit(event)

    sealed interface Event {
        data class LoginSuccess(val user: MiotUser) : Event
        data class LoginFailure(val e: Throwable) : Event
        data class ShowLoading(val show: Boolean) : Event
    }
}