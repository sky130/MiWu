package com.github.miwu.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import org.koin.core.annotation.KoinViewModel

sealed interface SplashState {
    object Loading : SplashState
    object NavigateToCrash : SplashState
    object NavigateToMain : SplashState
    object NavigateToLogin : SplashState
}

@KoinViewModel
class SplashViewModel(
    accountRepository: AccountRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<SplashState> = if (settingsRepository.hasPendingCrash) {
        flowOf(SplashState.NavigateToCrash)
    } else {
        accountRepository.loginState.map { it.toSplashState() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SplashState.Loading)

    private fun LoginState.toSplashState(): SplashState = when (this) {
        LoginState.Loading -> SplashState.Loading
        LoginState.LoggedOut, is LoginState.Failure -> SplashState.NavigateToLogin
        LoginState.Success, is LoginState.NetworkError -> SplashState.NavigateToMain
    }
}
