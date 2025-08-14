package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.ui.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSetting.isCrash.value){
            start<CrashActivity>()
            return finish()
        }
        if (AppSetting.userId.value.isEmpty()){
            start<LoginActivity>()
        }else{
            start<MainActivity>()
        }
        finish()
    }

}