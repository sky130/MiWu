package com.github.miwu.ui.login

import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrErrorCorrectionLevel
import com.github.alexzhirkevich.customqrgenerator.style.Color
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.miwu.R
import com.github.miwu.databinding.ActivityLoginBinding as Binding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.about.help.HelpActivity
import com.github.miwu.ui.about.license.LicenseActivity
import com.github.miwu.ui.login.LoginViewModel.Event.LoginFailure
import com.github.miwu.ui.login.LoginViewModel.Event.LoginSuccess
import com.github.miwu.ui.login.LoginViewModel.Event.ShowLoading
import com.github.miwu.ui.login.dialog.LoadingDialog
import com.github.miwu.ui.main.MainActivity
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.string
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: LoginViewModel by viewModel()
    val showQrCode = MutableStateFlow(true)
    val height by lazy {
        @Suppress("DEPRECATION") windowManager.defaultDisplay.height
    }
    val options by lazy {
        createQrVectorOptions {
            padding = .15f
            errorCorrectionLevel = QrErrorCorrectionLevel.Low
            background {
                drawable = ContextCompat
                    .getDrawable(this@LoginActivity, R.drawable.bg_qr_code)
            }
        }
    }

    override fun init() {
        viewModel.event
            .onEach(::handleEvent)
            .launchIn(lifecycleScope)
        viewModel.qrcode
            .onEach(::handleQrCode)
            .launchIn(lifecycleScope)
        showQrCode
            .onEach(::handleDisplay)
            .launchIn(lifecycleScope)
    }

    suspend fun handleDisplay(show: Boolean) {
        binding.scroll.smoothScrollTo(0, height / 2)
        if (show) {
            viewModel.requestQRCodeLogin()
        } else {
            viewModel.cancelLogin()
        }
    }

    suspend fun handleQrCode(data: String) {
        data.takeIf(String::isNotEmpty)
            ?.let(QrData::Url)
            ?.let { QrCodeDrawable(it, options) }
            ?.let { binding.qrcode.setImageDrawable(it) }
            ?: binding.qrcode.setImageBitmap(createBitmap(1, 1))
    }

    suspend fun handleEvent(event: LoginViewModel.Event) {
        when (event) {
            is LoginFailure -> {
                R.string.login_failure_toast
                    .let(::getString)
                    .format(event.e.message ?: "unknown")
                    .toast()
            }

            is LoginSuccess -> {
                R.string.toast_login_success.toast()
                start<MainActivity>()
                finish()
            }

            is ShowLoading -> {
                if (event.show) {
                    LoadingDialog().show(supportFragmentManager)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.scroll.requestFocus()
    }

    fun startLicenseActivity() {
        start<LicenseActivity>()
    }

    fun startHelpActivity() {
        start<HelpActivity>()
    }

    fun changeDisplayMode() {
        showQrCode.value = !showQrCode.value
    }

}
