package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.auth.AuthService
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.logic.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.logic.usecase.scene.GetHomeScenesUseCase
import com.github.miwu.logic.usecase.state.MapFragmentStateUseCase
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Normal
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotScene


class MainViewModel(
    private val miotRepository: MiotRepository,
    cacheRepository: CacheRepository,
    localRepository: LocalRepository,
    private val authService: AuthService,
    private val getSortedDevices: GetSortedDevicesUseCase,
    private val getSortedRooms: GetSortedRoomsUseCase,
    private val getHomeScenes: GetHomeScenesUseCase,
    private val mapState: MapFragmentStateUseCase,
) : ViewModel() {

    val info = miotRepository.userInfo
    val home = miotRepository.currentHome
    val loginStatus = miotRepository.loginStatus
    val user = miotRepository.user
    val metadataHandler = cacheRepository.deviceMetadataHandler
    val icons = cacheRepository.icons

    val devices = getSortedDevices().asLiveData()
    val rooms = getSortedRooms().asLiveData()
    val scenes = getHomeScenes().asLiveData()

    val roomState = mapState(home) { it.rooms.isEmpty() }.asLiveData()
    val deviceState = mapState(home) { it.devices.isEmpty() }.asLiveData()
    val sceneState = mapState(home) { it.scenes.isEmpty() }.asLiveData()
    val localDeviceState = localRepository.deviceListFlow
        .map { if (it.isEmpty()) Empty else Normal }
        .asLiveData()
    val localDevices = localRepository.deviceListFlow.asLiveData()

    fun refreshHome() {
        miotRepository.refreshCurrentHome()
    }

    fun runScene(scene: MiotScene) {
        miotRepository.runScene(scene)
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
        }
    }
}
