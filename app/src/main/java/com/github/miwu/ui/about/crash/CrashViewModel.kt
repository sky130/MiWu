package com.github.miwu.ui.about.crash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.repository.CrashLogRepository
import com.github.miwu.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrashViewModel(
    private val crashLogRepository: CrashLogRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val mutableCrashText = MutableStateFlow("")
    val crashText: StateFlow<String> = mutableCrashText.asStateFlow()
    val crashPath get() = crashLogRepository.path

    fun load() {
        viewModelScope.launch {
            mutableCrashText.value = crashLogRepository.readLatest()
        }
    }

    fun acknowledgeCrash() {
        settingsRepository.hasPendingCrash = false
    }
}
