package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.miwu.R
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import com.github.miwu.logic.state.LoginState
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.databinding.ActivityMainBinding as Binding
import com.github.miwu.ui.main.adapter.MainViewPagerAdapter
import com.github.miwu.ui.main.fragment.DeviceFragment
import com.github.miwu.ui.main.fragment.MiWuFragment
import com.github.miwu.ui.main.fragment.SceneFragment
import com.github.miwu.ui.main.fragment.UserFragment
import com.github.miwu.utils.Logger
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ViewActivityX<Binding>(Binding::inflate), OnPageChangeListener {
    override val viewModel: MainViewModel by viewModel()
    val adapter = MainViewPagerAdapter(this)
    val logger = Logger()

    override fun init() {
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(this)
        checkLoginStatue()
    }

    fun checkLoginStatue() {
        viewModel.appRepository.loginStatus.onEach {
            when (it) {
                LoginState.Loading -> Unit
                LoginState.Success -> Unit

                is LoginState.Failure -> {
                    viewModel.dataStore.updateData { MiotUserSerializer.defaultValue }
                    R.string.auth_expired_plz_login_again.toast()
                    start<LoginActivity>()
                    finish()
                }

                is LoginState.NetworkError -> {
                    R.string.network_error_plz_check.toast()
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixle: Int) = Unit

    override fun onPageSelected(position: Int) {
        binding.title.setTitle(adapter.list[position].first)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit
}