package com.github.miwu.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiwu
import com.github.miwu.logic.repository.LocalRepository
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice

class DeviceViewModel(val localRepository: LocalRepository) : ViewModel() {
    fun addFavorite(device: MiotDevice) {
        localRepository.addDevice(device)
    }
}