package com.github.miwu

import android.app.Application
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import com.google.gson.Gson
import kndroidx.extension.start
import kndroidx.kndroidx
import miot.kotlin.Miot
import miot.kotlin.MiotManager

class MainApplication : Application() {

    companion object {
        val gson = Gson()
        lateinit var miotUser: Miot.MiotUser
        val Any.miot by lazy { MiotManager.from(miotUser) }
    }

    override fun onCreate() {
        super.onCreate()
        kndroidx {
            context = applicationContext
        }
        if (AppPreferences.userId.isNotEmpty()){
            AppPreferences.apply {
                miotUser = Miot.MiotUser(userId, securityToken, serviceToken)
            }
        }
    }
}