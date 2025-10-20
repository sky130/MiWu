package com.github.miwu

import android.app.Application
import android.provider.Settings
import android.util.Base64
import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.viewModelModule
import com.google.gson.Gson
import kndroidx.KndroidX
import kndroidx.kndroidxConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miwu.miot.MiotManager
import miwu.miot.model.MiotUser
import miwu.miot.normalModule
import miwu.miot.singleModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    val appRepository: AppRepository by inject()
    val manager: MiotManager by inject()
    
    override fun onCreate() {
        super.onCreate()
        configKoin() 
        configKndroidx()
        configManager()
        configMiotUser()
    }

    fun configMiotUser() {
        if (AppSetting.userId.value.isNotEmpty()) {
            AppSetting.apply {
                appRepository.miotUser =
                    MiotUser(
                        userId.value,
                        securityToken.value,
                        serviceToken.value,
                        androidId
                    )
            }
        }
    }

    fun configKndroidx() {
        kndroidxConfig {
            context = applicationContext
        }
    }

    fun configKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                singleModule, repositoryModule, normalModule, viewModelModule, databaseModule
            )
        }
    }

    fun configManager() {
        manager.Base64.config(encode = {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }, decode = {
            Base64.decode(it, Base64.NO_WRAP)
        })
    }

    companion object {
        @Suppress("HardwareIds")
        val androidId: String by lazy {
            Settings.Secure.getString(
                KndroidX.context.contentResolver, Settings.Secure.ANDROID_ID
            )
        }
        val appJob = Job()
        val appScope = CoroutineScope(appJob)
        val gson = Gson()
    }
}
