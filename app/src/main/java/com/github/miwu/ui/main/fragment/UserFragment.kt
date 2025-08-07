package com.github.miwu.ui.main.fragment

import com.github.miwu.databinding.FragmentMainSettingsBinding as Binding
import com.github.miwu.ui.about.AboutActivity
import com.github.miwu.ui.home.HomeActivity
import com.github.miwu.ui.main.MainViewModel
import kndroidx.extension.start
import kndroidx.fragment.ViewFragmentX
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class UserFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()

    fun startHomeActivity() {
        requireContext().start<HomeActivity>()
    }

    fun startAboutActivity() {
        requireContext().start<AboutActivity>()
    }

    fun logout() {
//        if (exit == 0) {
//            exit++
//            return "再点击一次即可退出登录\n重进应用时即可登录".toast()
//        }
//        AppPreferences.apply {
//            userId = ""
//            securityToken = ""
//            homeId = 0
//            homeUid = 0
//            serviceToken = ""
//        }
//        requireActivity().finish()
//        Handler(Looper.getMainLooper()).postDelayed({exitProcess(0)},500)
    }

    fun startSmartActivity() {
//        requireContext().start<SmartActivity>()
    }
}