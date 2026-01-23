package com.github.miwu.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.utils.Logger
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LoginViewModel(
    val loginProvider: MiotLoginProvider,
    val appRepository: AppRepository,
    val miotUserDataStore: MiotUserDataStore
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
            runCatching {
                loginProvider.login(user, pwd).getOrThrow()
            }.onFailure { e ->
                loginFailure(e)
            }.onSuccess { user ->
                loginSuccess(user)
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
                val response = loginProvider
                    .generateLoginQrCode()
                    .getOrThrow()
                val qrcode = response.toQrCode()
                    ?: error("generate login qrcode failure, response=${response}")
                logger.info(
                    "generate login qrcode successfully, qrcode data: {}, login url: {}",
                    qrcode.data,
                    qrcode.loginUrl
                )
                _qrcode.emit(qrcode.data)
                loginProvider
                    .loginByQrCode(qrcode.loginUrl)
                    .getOrThrow()
            }.onFailure { e ->
                if (e is SocketTimeoutException || e is TimeoutException) {
                    requestQRCodeLogin()
                } else {
                    loginFailure(e)
                }
            }.onSuccess { user ->
                loginSuccess(user)
            }
        }
    }

    private suspend fun loginSuccess(user: MiotUser) {
        logger.info("login successfully")
        val user = user.copy(deviceId = MainApplication.androidId)
        miotUserDataStore.updateData { user }
        event(Event.LoginSuccess(user))
    }

    suspend fun loginFailure(e: Throwable) {
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