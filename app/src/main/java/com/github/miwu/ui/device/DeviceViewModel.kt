package com.github.miwu.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.model.MiwuDatabaseDevice.Companion.toMiwu
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice

class DeviceViewModel(val database: AppDatabase) : ViewModel() {
    fun addFavorite(device: MiotDevice) {
        viewModelScope.launch {
            database.device().insert(device.toMiwu())
        }
    }
}