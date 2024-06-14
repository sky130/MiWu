package com.github.miwu

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.github.miwu.logic.handler.CrashHandler
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.miot.initClassList
import com.google.gson.Gson
import kndroidx.KndroidX
import kndroidx.extension.log
import kndroidx.kndroidx
import kndroidx.kndroidxConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miot.kotlin.Miot
import miot.kotlin.MiotManager
import android.util.Base64

class MainApplication : Application() {

    @SuppressLint("HardwareIds")
    companion object {
        val appJob = Job()
        val appScope = CoroutineScope(appJob)
        val gson = Gson()
        lateinit var miotUser: Miot.MiotUser
        val Any.miot by lazy { MiotManager.from(miotUser) }

        val androidId by lazy {
            Settings.Secure.getString(
                KndroidX.context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        kndroidxConfig {
            context = applicationContext
        }
        MiotManager.configBase64{
            Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
        }
        initClassList()
        CrashHandler.instance.init(this)
        if (AppPreferences.userId.isNotEmpty()) {
            AppPreferences.apply {
                miotUser = Miot.MiotUser(userId, securityToken, serviceToken, androidId)
            }
        }
        MiotManager.log
    }
}
