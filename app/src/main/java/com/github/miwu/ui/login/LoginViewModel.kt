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
import com.github.miwu.logic.repository.AppRepository
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.MiotManager
import miwu.miot.MiotManagerImpl
import miwu.miot.model.MiotUser
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(internal val manager : MiotManager, val appRepository: AppRepository) : ViewModel() {
    private val job = Job()
    private val scope = CoroutineScope(job)
    private val _qrcode = MutableSharedFlow<Bitmap>()
    private val generator = QrCodeGenerator()
    private val _miotUser = MutableSharedFlow<MiotUser>()

    val user = ObservableField("")
    val password = ObservableField("")

    val isQrCode = MutableLiveData(true)
    val qrcode = _qrcode.asLiveData()
    val miotUser = _miotUser.asSharedFlow()

    fun qrcode() {
        job.cancelChildren()
        scope.launch(Dispatchers.IO) {
            runCatching {
                _qrcode.emit(createBitmap(1, 1))
                val qrcode = manager.Login.generateLoginQrCode().toQrCode()
                val data = QrData.Url(qrcode.data)
                _qrcode.emit(generator.generateQrCode(data, options))
                manager.Login.loginByQrCode(qrcode.loginUrl).getOrThrow()
            }.onFailure { e ->
                if (e is SocketTimeoutException || e is TimeoutException) return@launch qrcode()
                withContext(Dispatchers.Main){
                    "登录失败,原因可能为${e.message ?: "unknown"}".toast()
                }
            }.onSuccess {
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