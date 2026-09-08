package com.github.miwu

import android.app.Application
import androidx.core.content.edit
import com.github.miwu.platform.crash.CrashHandler
import com.github.miwu.utils.LazyLogger
import kndroidx.kndroidxConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.modules
import org.koin.plugin.module.dsl.startKoin

class MainApplication : Application() {
    val logger by LazyLogger()
    private val crashHandler: CrashHandler by inject()

    override fun onCreate() {
        super.onCreate()
        configKndroidx()
        configKoin()
        crashHandler.install()
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
        startKoin<KoinApp> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
