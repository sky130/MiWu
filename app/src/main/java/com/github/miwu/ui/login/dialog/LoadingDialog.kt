package com.github.miwu.ui.login.dialog

import kndroidx.dialog.BaseDialog
import com.github.miwu.databinding.DialogLoadingBinding as Binding

class LoadingDialog : BaseDialog<Binding>(Binding::inflate) {
    override fun init() = Unit
}
