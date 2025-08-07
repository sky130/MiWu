package com.github.miwu

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import android.util.Base64
import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.repository.AppRepository
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

    @SuppressLint("HardwareIds")
    companion object {
        val appJob = Job()
        val appScope = CoroutineScope(appJob)
        val gson = Gson()
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
        koin()
        val manager: MiotManager by inject()
        manager.Base64.config(
            encode = {
                Base64.encodeToString(it, Base64.NO_WRAP)
            },
            decode = {
                Base64.decode(it, Base64.NO_WRAP)
            }
        )
        if (AppSetting.userId.isNotEmpty()) {
            AppSetting.apply {
                appRepository.miotUser = MiotUser(userId, securityToken, serviceToken, androidId)
            }
        }
    }

    fun koin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                singleModule,
                normalModule,
                viewModelModule,
                databaseModule
            )
        }
    }
}
