package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.domain.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.domain.usecase.scene.GetHomeScenesUseCase
import com.github.miwu.ui.common.mapFragmentState
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotScene
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val homeRepository: HomeRepository,
    metadataRepository: DeviceMetadataRepository,
    favoriteDeviceRepository: FavoriteDeviceRepository,
    private val accountRepository: AccountRepository,
    private val getSortedDevices: GetSortedDevicesUseCase,
    private val getSortedRooms: GetSortedRoomsUseCase,
    private val getHomeScenes: GetHomeScenesUseCase,
) : ViewModel() {

    val info = homeRepository.userInfo
    val home = homeRepository.currentHome
    val loginStatus = accountRepository.loginState
    val metadataHandler = metadataRepository.metadata

    val devices = getSortedDevices().asLiveData()
    val rooms = getSortedRooms().asLiveData()
    val scenes = getHomeScenes().asLiveData()

    val roomState = home.mapFragmentState { it.rooms.isEmpty() }.asLiveData()
    val deviceState = home.mapFragmentState { it.devices.isEmpty() }.asLiveData()
    val sceneState = home.mapFragmentState { it.scenes.isEmpty() }.asLiveData()
    val localDeviceState = favoriteDeviceRepository.devices.mapFragmentState().asLiveData()
    val localDevices = favoriteDeviceRepository.devices.asLiveData()

    fun refreshHome() {
        viewModelScope.launch { homeRepository.refreshCurrentHome() }
    }

    fun runScene(scene: MiotScene) {
        viewModelScope.launch { homeRepository.runScene(scene) }
    }

    fun logout() {
        viewModelScope.launch {
            accountRepository.logout()
        }
    }
}
