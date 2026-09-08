package com.github.miwu.ui.room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.ui.common.mapFragmentState
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class RoomViewModel(
    private val homeRepository: HomeRepository,
    private val getSortedDevices: GetSortedDevicesUseCase,
    private val metadataRepository: DeviceMetadataRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val room = savedStateHandle.get<String>("room").orEmpty()
    val home = homeRepository.currentHome
    val devices = getSortedDevices(room).asLiveData()
    val metadataHandler = metadataRepository.metadata
    val deviceState = home.mapFragmentState { it.rooms[room].isNullOrEmpty() }.asLiveData()

    fun loadDevice() {
        viewModelScope.launch { homeRepository.refreshCurrentHome() }
    }
}
