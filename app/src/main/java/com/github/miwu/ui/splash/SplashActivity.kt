package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.miwu.R
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.utils.Logger
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.logic.state.LoginState
import com.github.miwu.ui.about.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.model.MiotUser
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