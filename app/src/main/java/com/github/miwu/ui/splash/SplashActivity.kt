package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.about.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import com.github.miwu.utils.Logger
import kndroidx.extension.start
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    val dataStore: MiotUserDataStore by inject()
    val logger = Logger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSetting.isCrash.value) {
            start<CrashActivity>()
            return finish()
        }
        lifecycleScope.launch {
            if (dataStore.isLogin()) {
                start<MainActivity>()
            } else {
                logger.info("user is not login")
                start<LoginActivity>()
            }
            finish()
        }
    }

}