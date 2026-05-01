package com.github.miwu.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.usecase.state.MapFragmentStateUseCase
import kotlinx.coroutines.flow.take

class EditFavoriteViewModel(
    private val localRepository: LocalRepository,
    cacheRepository: CacheRepository,
    mapState: MapFragmentStateUseCase,
) : ViewModel() {
    val metadataHandler = cacheRepository.deviceMetadataHandler
    val devices = localRepository.deviceListFlow
        .take(1)
        .asLiveData()
    val deviceState = mapState(localRepository.deviceListFlow).asLiveData()

    fun updateSortIndices(list: List<FavoriteDevice>) {
        localRepository.updateSortIndices(list)
    }

    fun remove(item: FavoriteDevice) {
        localRepository.removeDevice(item)
    }
}
