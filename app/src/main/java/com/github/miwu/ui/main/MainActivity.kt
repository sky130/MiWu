package com.github.miwu.ui.main

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.miwu.R
import com.github.miwu.domain.model.LoginState
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.main.adapter.MainViewPagerAdapter
import com.github.miwu.utils.Logger
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityMainBinding as Binding

class MainActivity : ViewActivityX<Binding>(Binding::inflate), OnPageChangeListener {
    override val viewModel: MainViewModel by viewModel()
    val adapter = MainViewPagerAdapter(this)
    val logger = Logger()

    override fun init() {
        binding.indicator.dotSize = adapter.list.size
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(this)
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginStatus.collect {
                    when (it) {
                        LoginState.Loading, LoginState.Success -> Unit
                        LoginState.LoggedOut -> navigateToLogin()
                        is LoginState.Failure -> {
                            viewModel.logout()
                            R.string.auth_expired_plz_login_again.toast()
                            navigateToLogin()
                        }

                        is LoginState.NetworkError -> R.string.network_error_plz_check.toast()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        start<LoginActivity>()
        finish()
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixle: Int) = Unit

    override fun onPageSelected(position: Int) {
        binding.indicator.index = position
        binding.title.setTitle(adapter.list[position].first)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit
}
