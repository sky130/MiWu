package com.github.miwu.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Normal
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class EditFavoriteViewModel(
    val miotRepository: MiotRepository,
    val localRepository: LocalRepository,
    val cacheRepository: CacheRepository
) : ViewModel() {
    val metadataHandler = cacheRepository.deviceMetadataHandler
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