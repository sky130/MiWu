package com.github.miwu.ui.login.dialog

import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.login.LoginViewModel
import com.github.miwu.ui.main.MainActivity
import kndroidx.dialog.BaseDialog
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.DialogLoadingBinding as Binding

class LoadingDialog(private val user: String, private val pwd: String) :
    BaseDialog<Binding>(Binding::inflate) {

    val viewModel: LoginViewModel by viewModel()

    override fun init() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.manager.Login.login(user, pwd).also {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        R.string.toast_login_failure.toast()
                        dismiss()
                    } else {
                        AppSetting.apply {
                            this.userId = it.userId
                            this.securityToken = it.securityToken
                            this.serviceToken = it.serviceToken
                        }
                        viewModel.appRepository.miotUser = it.copy(deviceId = MainApplication.androidId)
                        R.string.toast_login_success.toast()
                        requireActivity().start<MainActivity>()
                        dismiss()
                        requireActivity().finish()
                    }
                }

            }
        }
    }

}