package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.MainApplication
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start
import miot.kotlin.Miot

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppPreferences.isCrash){
            start<CrashActivity>()
            return finish()
        }
        if (AppPreferences.userId.isEmpty()){
            start<LoginActivity>()
        }else{
            start<MainActivity>()
        }
        finish()
    }

}