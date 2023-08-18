package io.github.sky130.miwu

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.github.sky130.miwu.logic.dao.UserDAO
import io.github.sky130.miwu.logic.model.user.LoginMsg

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