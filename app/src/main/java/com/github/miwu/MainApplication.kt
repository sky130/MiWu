package com.github.miwu

import android.app.Application
import com.github.miwu.logic.handler.CrashHandler
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.miot.initClassList
import com.google.gson.Gson
import kndroidx.kndroidx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miot.kotlin.Miot
import miot.kotlin.MiotManager

class MainApplication : Application() {

    companion object {
        val appJob = Job()
        val appScope = CoroutineScope(appJob)
        val gson = Gson()
        lateinit var miotUser: Miot.MiotUser
        val Any.miot by lazy { MiotManager.from(miotUser) }
    }

    override fun onCreate() {
        super.onCreate()
        kndroidx {
            context = applicationContext
        }
        initClassList()
        CrashHandler.instance.init(this)
        if (AppPreferences.userId.isNotEmpty()) {
            AppPreferences.apply {
                miotUser = Miot.MiotUser(userId, securityToken, serviceToken)
            }
        }
    }
}