package com.github.miwu.ui.login

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.databinding.ActivityLoginBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.help.HelpActivity
import com.github.miwu.ui.license.LicenseActivity
import com.github.miwu.ui.login.dialog.LoadingDialog
import com.github.miwu.ui.main.MainActivity
import com.github.miwu.viewmodel.LoginViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginActivity : ViewActivityX<ActivityLoginBinding, LoginViewModel>() {

    fun login() =
        viewModel.apply {
            LoadingDialog(user.get()!!, password.get()!!)
                .show(supportFragmentManager)
        }

    val handler = Handler(Looper.getMainLooper())


    override fun init() {
        viewModel.qrcode()
        viewModel.qrcodeMiot.onEach {
            AppPreferences.apply {
                this.userId = it.userId
                this.securityToken = it.securityToken
                this.serviceToken = it.serviceToken
            }
            MainApplication.miotUser = it.copy(deviceId = MainApplication.androidId)
            R.string.toast_login_success.toast()
            start<MainActivity>()
            finish()
        }.launchIn(lifecycleScope)
        viewModel.isQrCode.observe(this) {
           handler.postDelayed({ binding.scroll.smoothScrollTo(0, windowManager.defaultDisplay.height / 2)},50)
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

