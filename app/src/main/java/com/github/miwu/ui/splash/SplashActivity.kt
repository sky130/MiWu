package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.miwu.ui.about.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.onEach { state ->
            when (state) {
                SplashState.NavigateToCrash -> {
                    start<CrashActivity>()
                    finish()
                }
                SplashState.NavigateToMain -> {
                    start<MainActivity>()
                    finish()
                }
                SplashState.NavigateToLogin -> {
                    start<LoginActivity>()
                    finish()
                }
                SplashState.Loading -> Unit
            }
        }.launchIn(lifecycleScope)
    }

}