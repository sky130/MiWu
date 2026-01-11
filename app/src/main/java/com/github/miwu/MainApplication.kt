package com.github.miwu

import android.app.Application
import android.provider.Settings
import android.util.Base64
import com.github.miwu.ktx.LazyLogger
import com.github.miwu.ktx.mask
import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.viewModelModule
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
    val logger by LazyLogger()
    val appRepository: AppRepository by inject()
    val manager: MiotManager by inject()

    override fun onCreate() {
        super.onCreate()
        configKoin()
        configKndroidx()
        configMiotManager()
        configMiotUser()
    }

    fun configMiotUser() {
        logger.info("Config miot user")
        if (AppSetting.userId.value.isNotEmpty()) {
            AppSetting.apply {
                appRepository.miotUser =
                    MiotUser(
                        userId.value,
                        securityToken.value,
                        serviceToken.value,
                        androidId
                    ).apply {
                        logger.info(
                            "Current MiotUser: userId={}, securityToken={}, serviceToken={}",
                            userId,
                            securityToken.mask(),
                            serviceToken.mask(),
                        )
                    }
            }
        }
    }

    fun configKndroidx() {
        logger.info("Config kndroidx")
        kndroidxConfig {
            context = applicationContext
        }
    }

    fun configKoin() {
        logger.info("Config koin")
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                singleModule, repositoryModule, normalModule, viewModelModule, databaseModule
            )
        }
    }

    fun configMiotManager() {
        logger.info("Config miot manager")
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
    }
}
