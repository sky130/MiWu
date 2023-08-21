package com.github.miwu

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.github.miwu.logic.dao.UserDAO
import com.github.miwu.logic.model.user.LoginMsg

class MainApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var loginMsg: LoginMsg
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        loginMsg = UserDAO.getLocalUser()
    }

}