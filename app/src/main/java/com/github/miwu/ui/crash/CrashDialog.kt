package com.github.miwu.ui.crash

import com.github.miwu.basic.AppDialog
import com.github.miwu.databinding.DialogCrashBinding
import com.github.miwu.viewmodel.CrashViewModel
import kndroidx.extension.compareTo

class CrashDialog(val str:String): AppDialog<DialogCrashBinding, CrashViewModel>() {


    override fun init() {
        binding.text <= str
        binding.title.setTitleOnClick{
            exit()
        }
    }
}