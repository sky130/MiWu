package com.github.miwu.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.miwu.ui.about.crash.CrashActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.MainActivity
import kndroidx.extension.start
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        SplashState.NavigateToCrash -> navigateTo<CrashActivity>()
                        SplashState.NavigateToMain -> navigateTo<MainActivity>()
                        SplashState.NavigateToLogin -> navigateTo<LoginActivity>()
                        SplashState.Loading -> Unit
                    }
                }
            }
        }
    }

    private inline fun <reified T : android.app.Activity> navigateTo() {
        start<T>()
        finish()
    }

}
