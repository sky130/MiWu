package com.github.miwu.ui.login

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrErrorCorrectionLevel
import com.github.alexzhirkevich.customqrgenerator.createQrOptions
import com.github.miwu.R
import com.github.miwu.utils.Logger
import com.github.miwu.logic.repository.AppRepository
import kndroidx.extension.string
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(
    val loginProvider: MiotLoginProvider,
    val appRepository: AppRepository
) :
    ViewModel() {
    private val job = Job()
    private val scope = CoroutineScope(job)
    private val _qrcode = MutableSharedFlow<Bitmap>()
    private val generator = QrCodeGenerator()
    private val _miotUser = MutableSharedFlow<MiotUser>()
    private val logger = Logger()
    val user = ObservableField("")
    val password = ObservableField("")

    val isQrCode = MutableLiveData(true)
    val qrcode = _qrcode.asLiveData()
    val miotUser = _miotUser.asSharedFlow()

    fun qrcode() {
        job.cancelChildren()
        logger.info("Request for a login qrcode")
        scope.launch(Dispatchers.IO) {
            runCatching {
                // TODO 重构这里
                _qrcode.emit(createBitmap(1, 1))
                val response = loginProvider.generateLoginQrCode().getOrNull()
                    ?: return@launch
                logger.info("generate login qrcode successfully, data={}", response)
                val qrcode = response.toQrCode()
                    ?: return@launch logger.warn("generate login qrcode failure, data={}", response)
                val data = QrData.Url(qrcode.data)
                logger.info("qrcode data: {}, login url: {}", data, qrcode.loginUrl)
                _qrcode.emit(generator.generateQrCode(data, options))
                loginProvider.loginByQrCode(qrcode.loginUrl).getOrThrow()
            }.onFailure { e ->
                logger.warn("login failure, cause by {}", e.stackTraceToString())
                if (e is SocketTimeoutException || e is TimeoutException) return@launch qrcode()
                withContext(Dispatchers.Main) {
                    R.string.login_failure_toast.string.format(e.message ?: "unknown").toast()
                }
            }.onSuccess {
                logger.info("login successfully")
                _miotUser.emit(it)
            }
        }
    }

    fun change() {
        isQrCode.value = !isQrCode.value!!
    }

    private val options = createQrOptions(512, 512, 0.1f) {
        errorCorrectionLevel = QrErrorCorrectionLevel.Low
    }
}