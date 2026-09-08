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
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::LicenseViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::HelpViewModel)
    viewModelOf(::EditFavoriteViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::CrashViewModel)
    viewModel {
        DeviceViewModel(
            get(), get(), get(), get(), get(), get(),
            get(appMainDispatcher),
            get(appIoDispatcher),
        )
    }
    viewModel { params ->
        RoomViewModel(get(), get(), get(), params[0])
    }
}
