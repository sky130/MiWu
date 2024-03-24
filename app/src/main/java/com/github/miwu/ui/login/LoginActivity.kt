package com.github.miwu.ui.login

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

