package com.github.miwu.ui.main.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.github.miwu.MainApplication
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.databinding.FragmentMainSettingsBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.about.AboutActivity
import com.github.miwu.ui.home.HomeActivity
import com.github.miwu.ui.login.LoginActivity
import com.github.miwu.ui.smart.SmartActivity
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import miot.kotlin.Miot
import kotlin.system.exitProcess

class UserFragment : ViewFragmentX<FragmentMainSettingsBinding, MainViewModel>() {
    private var exit = 0

    override fun init() {
        viewModel.loadInfo()
    }

    fun startHomeActivity() {
        requireContext().start<HomeActivity>()
    }

    fun startAboutActivity() {
        requireContext().start<AboutActivity>()
    }

    fun logout() {
        if (exit == 0) {
            exit++
            return "再点击一次即可退出登录\n重进应用时即可登录".toast()
        }
        AppPreferences.apply {
            userId = ""
            securityToken = ""
            homeId = 0
            homeUid = 0
            serviceToken = ""
        }
        requireActivity().finish()
        Handler(Looper.getMainLooper()).postDelayed({exitProcess(0)},500)
    }

    fun startSmartActivity() {
        requireContext().start<SmartActivity>()
    }
}