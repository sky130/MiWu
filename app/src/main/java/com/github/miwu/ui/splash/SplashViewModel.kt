package com.github.miwu.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.setting.AppSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SplashState {
    object Loading : SplashState
    object NavigateToCrash : SplashState
    object NavigateToMain : SplashState
    object NavigateToLogin : SplashState
}

class SplashViewModel(
    private val dataStore: MiotUserDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    init {
        checkNavigation()
    }

    private fun checkNavigation() {
        viewModelScope.launch {
            if (AppSetting.isCrash.value) {
                _state.value = SplashState.NavigateToCrash
            } else if (dataStore.isLogin()) {
                _state.value = SplashState.NavigateToMain
            } else {
                _state.value = SplashState.NavigateToLogin
            }
        }
    }
}
