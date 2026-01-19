package com.github.miwu

import android.app.Application
import android.provider.Settings
import com.github.miwu.utils.LazyLogger
import com.github.miwu.utils.mask
import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.viewModelModule
import kndroidx.KndroidX
import kndroidx.kndroidxConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.Provider
import miwu.miot.model.MiotUser
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    val logger by LazyLogger()
    val appRepository: AppRepository by inject()

    override fun onCreate() {
        super.onCreate()
        configKoin()
        configKndroidx()
        configMiotUser()
    }

    fun configMiotUser() {
        logger.info("Config miot user")
        AppSetting.apply {
            if (listOf(
                    userId,
                    cUserId,
                    nonce,
                    ssecurity,
                    psecurity,
                    passToken,
                    serviceToken
                ).any { it.value.isEmpty() }
            ) {
                return logger.info("user is not login")
            }
            MiotUser(
                userId.value,
                cUserId.value,
                nonce.value,
                ssecurity.value,
                psecurity.value,
                passToken.value,
                serviceToken.value,
                androidId
            ).apply {
                logger.info(
                    "Current MiotUser: userId={}, securityToken={}, serviceToken={}",
                    userId, ssecurity.mask(), serviceToken.mask(),
                )
            }.also { appRepository.miotUser = it }
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
                repositoryModule,
                viewModelModule,
                databaseModule,
                module {
                    single<Job> { Job() }
                    single { CoroutineScope(get<Job>()) }
                }
            )
            modules(
                MiotApiKoinModule.KMP.Client,
                MiotApiKoinModule.KMP.Provider
            )
        }
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
