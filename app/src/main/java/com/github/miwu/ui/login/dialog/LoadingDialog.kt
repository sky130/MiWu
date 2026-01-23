package com.github.miwu.ui.login.dialog

import androidx.lifecycle.lifecycleScope
import com.github.miwu.ui.login.LoginViewModel
import com.github.miwu.ui.login.LoginViewModel.Event.ShowLoading
import kndroidx.dialog.BaseDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.DialogLoadingBinding as Binding

class LoadingDialog : BaseDialog<Binding>(Binding::inflate) {
    val viewModel: LoginViewModel by viewModel()

    override fun init() {
        viewModel.event.onEach { event ->
            if (event is ShowLoading && !event.show) {
                dismiss()
            }
        }.launchIn(lifecycleScope)
    }
}