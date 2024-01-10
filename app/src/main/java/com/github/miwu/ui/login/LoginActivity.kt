package com.github.miwu.ui.login

import com.github.miwu.databinding.ActivityLoginBinding
import com.github.miwu.ui.login.dialog.LoadingDialog
import com.github.miwu.viewmodel.LoginViewModel
import kndroidx.activity.ViewActivityX

class LoginActivity : ViewActivityX<ActivityLoginBinding, LoginViewModel>() {

    fun login() =
        viewModel.apply {
            LoadingDialog(user.get()!!, password.get()!!)
                .show(supportFragmentManager)
        }

}

