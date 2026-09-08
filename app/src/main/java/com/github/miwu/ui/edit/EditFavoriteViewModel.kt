package com.github.miwu.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.ui.common.mapFragmentState
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice

class EditFavoriteViewModel(
    private val favoriteDeviceRepository: FavoriteDeviceRepository,
    metadataRepository: DeviceMetadataRepository,
) : ViewModel() {
    val metadataHandler = metadataRepository.metadata
    val devices = favoriteDeviceRepository.devices.asLiveData()
    val deviceState = favoriteDeviceRepository.devices.mapFragmentState().asLiveData()

    fun updateSortIndices(list: List<MiotDevice>) {
        viewModelScope.launch { favoriteDeviceRepository.updateOrder(list) }
    }

    fun remove(item: MiotDevice) {
        viewModelScope.launch { favoriteDeviceRepository.remove(item) }
    }
}
