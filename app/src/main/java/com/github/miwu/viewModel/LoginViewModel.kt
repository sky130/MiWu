package com.github.miwu.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kndroidx.extension.log

class LoginViewModel : ViewModel() {

    val user = ObservableField("")
    val password = ObservableField("")

    fun logMsg(){
        "${user.get() ?: "null"} ${password.get() ?: "null"}".log("login").d()
    }
}