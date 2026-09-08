package com.github.miwu.di

import com.github.miwu.ui.about.AboutViewModel
import com.github.miwu.ui.about.crash.CrashViewModel
import com.github.miwu.ui.about.help.HelpViewModel
import com.github.miwu.ui.about.license.LicenseViewModel
import com.github.miwu.ui.device.DeviceViewModel
import com.github.miwu.ui.edit.EditFavoriteViewModel
import com.github.miwu.ui.home.HomeViewModel
import com.github.miwu.ui.login.LoginViewModel
import com.github.miwu.ui.main.MainViewModel
import com.github.miwu.ui.room.RoomViewModel
import com.github.miwu.ui.splash.SplashViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val viewModelModule = module {
    viewModel<SplashViewModel>()
    viewModel<MainViewModel>()
    viewModel<LicenseViewModel>()
    viewModel<HomeViewModel>()
    viewModel<HelpViewModel>()
    viewModel<EditFavoriteViewModel>()
    viewModel<AboutViewModel>()
    viewModel<LoginViewModel>()
    viewModel<CrashViewModel>()
    viewModel<DeviceViewModel>()
    viewModel<RoomViewModel>()
}
