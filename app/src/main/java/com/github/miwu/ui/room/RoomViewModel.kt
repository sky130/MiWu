package com.github.miwu.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.logic.usecase.state.MapFragmentStateUseCase
import com.github.miwu.utils.Logger


class RoomViewModel(
    private val miotRepository: MiotRepository,
    cacheRepository: CacheRepository,
    private val getSortedDevices: GetSortedDevicesUseCase,
    private val mapState: MapFragmentStateUseCase,
    val room: String,
) : ViewModel() {
    private val logger = Logger()
    val info get() = miotRepository.user
    val home = miotRepository.currentHome
    val devices = getSortedDevices(room).asLiveData()
    val metadataHandler = cacheRepository.deviceMetadataHandler
    val deviceState = mapState(home) { it.devices.isEmpty() }.asLiveData()

    fun loadDevice() {
        miotRepository.refreshCurrentHome()
    }
}
