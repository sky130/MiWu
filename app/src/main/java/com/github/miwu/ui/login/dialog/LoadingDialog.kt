package com.github.miwu.ui.login.dialog

import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.databinding.DialogLoadingBinding
import com.github.miwu.basic.AppDialog
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.main.MainActivity
import com.github.miwu.viewmodel.LoginViewModel
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.MiotManager

class LoadingDialog(private val user: String, private val pwd: String) :
    AppDialog<DialogLoadingBinding, LoginViewModel>() {

    override fun init() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            MiotManager.login(user, pwd).also {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        R.string.toast_login_failure.toast()
                        exit()
                    } else {
                        AppPreferences.apply {
                            this.userId = it.userId
                            this.securityToken = it.securityToken
                            this.serviceToken = it.serviceToken
                        }
                        MainApplication.miotUser = it.copy(deviceId = MainApplication.androidId)
                        R.string.toast_login_success.toast()
                        exit {
                            requireActivity().start<MainActivity>()
                            requireActivity().finish()
                        }
                    }
                }

            }
        }
    }

}