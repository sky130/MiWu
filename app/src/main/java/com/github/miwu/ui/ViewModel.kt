package com.github.miwu.ui

import com.github.miwu.ui.about.AboutViewModel
import com.github.miwu.ui.crash.CrashViewModel
import com.github.miwu.ui.device.DeviceViewModel
import com.github.miwu.ui.favorite.EditFavoriteViewModel
import com.github.miwu.ui.help.HelpViewModel
import com.github.miwu.ui.home.HomeViewModel
import com.github.miwu.ui.license.LicenseViewModel
import com.github.miwu.ui.login.LoginViewModel
import com.github.miwu.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(get(), get())
    }
    viewModel {
        LoginViewModel(get(), get())
    }
    viewModel {
        LicenseViewModel()
    }
    viewModel {
        HomeViewModel(get())
    }
    viewModel {
        HelpViewModel()
    }
    viewModel {
        EditFavoriteViewModel(get(),get())
    }
    viewModel {
        DeviceViewModel(get())
    }
    viewModel {
        CrashViewModel()
    }
    viewModel {
        AboutViewModel()
    }

}