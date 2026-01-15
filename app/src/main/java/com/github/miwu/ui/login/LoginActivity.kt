package com.github.miwu.ui.login

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.databinding.ActivityLoginBinding as Binding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.about.help.HelpActivity
import com.github.miwu.ui.about.license.LicenseActivity
import com.github.miwu.ui.login.dialog.LoadingDialog
import com.github.miwu.ui.main.MainActivity
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: LoginViewModel by viewModel()
    val appRepository: AppRepository by inject()

    fun login() =
        viewModel.apply {
            LoadingDialog(user.get()!!, password.get()!!)
                .show(supportFragmentManager)
        }

    val handler = Handler(Looper.getMainLooper())

    override fun init() {
        viewModel.qrcode()
        viewModel.miotUser.onEach {
            AppSetting.apply {
                userId.value = it.userId
                securityToken.value = it.securityToken
                serviceToken.value = it.serviceToken
            }
            appRepository.miotUser = it.copy(deviceId = MainApplication.androidId)
            R.string.toast_login_success.toast()
            start<MainActivity>()
            finish()
        }.launchIn(lifecycleScope)
        viewModel.isQrCode.observe(this) {
            handler.postDelayed({
                binding.scroll.smoothScrollTo(
                    0,
                    windowManager.defaultDisplay.height / 2
                )
            }, 50)
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

}
