package com.github.miwu.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Normal
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class EditFavoriteViewModel(
    val appRepository: AppRepository,
    val localRepository: LocalRepository,
    val deviceRepository: DeviceRepository,
) : ViewModel() {
    val metadataHandler = deviceRepository.deviceMetadataHandler
        .asLiveData()
    val devices = localRepository.deviceListFlow
        .take(1)
        .asLiveData()
    val deviceState = localRepository.deviceListFlow
        .map { if (it.isEmpty()) Empty else Normal }
        .asLiveData()

    fun updateSortIndices(list: List<FavoriteDevice>) {
        localRepository.updateSortIndices(list)
    }

    fun remove(item: FavoriteDevice) {
        localRepository.removeDevice(item)
    }
}