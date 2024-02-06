package com.github.miwu.ui.login

import com.github.miwu.databinding.ActivityLoginBinding
import com.github.miwu.ui.help.HelpActivity
import com.github.miwu.ui.license.LicenseActivity
import com.github.miwu.ui.login.dialog.LoadingDialog
import com.github.miwu.viewmodel.LoginViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start

class LoginActivity : ViewActivityX<ActivityLoginBinding, LoginViewModel>() {

    fun login() =
        viewModel.apply {
            LoadingDialog(user.get()!!, password.get()!!)
                .show(supportFragmentManager)
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

