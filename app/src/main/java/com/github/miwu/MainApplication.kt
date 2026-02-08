package com.github.miwu

import android.app.Application
import android.provider.Settings
import com.github.miwu.utils.LazyLogger
import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.ui.viewModelModule
import kndroidx.KndroidX
import kndroidx.kndroidxConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.Provider
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import androidx.core.content.edit
import miwu.miot.Provider

class MainApplication : Application() {
    val logger by LazyLogger()

    override fun onCreate() {
        super.onCreate()
        configKoin()
        configKndroidx()
        deleteLegacyData()
    }

    fun deleteLegacyData() {
        logger.info("Clear legacy preferences")
        getSharedPreferences("app", MODE_PRIVATE).edit(commit = true) { clear() }
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
            modules(appModule)
        }
    }

    companion object {
        @Suppress("HardwareIds")
        val androidId: String by lazy {
            Settings.Secure.getString(
                KndroidX.context.contentResolver, Settings.Secure.ANDROID_ID
            )
        }
    }
}
