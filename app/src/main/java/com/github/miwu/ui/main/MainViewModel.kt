package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Error
import com.github.miwu.ui.main.state.FragmentState.Loading
import com.github.miwu.ui.main.state.FragmentState.Normal
import com.github.miwu.utils.Logger
import fr.haan.resultat.fold
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.KoinViewModel


class MainViewModel(
    val appRepository: AppRepository,
    val localRepository: LocalRepository,
    val deviceRepository: DeviceRepository,
    val dataStore: MiotUserDataStore
) : ViewModel() {
    private val logger = Logger()
    val metadataHandler = deviceRepository.deviceMetadataHandler
        .asLiveData()
    val localDevices = localRepository.deviceList
        .asLiveData()
    val localDeviceState = localRepository.deviceList
        .map { if (it.isEmpty()) Empty else Normal }
        .asLiveData()
    val scenes = appRepository.scenes
        .map { it.getOrNull() ?: emptyList() }
        .asLiveData()
    val devices = appRepository.devices
        .map { it.getOrNull() ?: emptyList() }
        .map { device ->
            device.sortedWith(
                compareBy(
                    { !it.isOnline },
                    { deviceRepository.getRoom(it.did) },
                    { it.name.lowercase() }
                )
            )
        }
        .asLiveData()
    val deviceState = appRepository.devices
        .map { resultat ->
            resultat.fold(
                onSuccess = { if (it.isEmpty()) Empty else Normal },
                onFailure = { Error },
                onLoading = { Loading }
            )
        }
        .asLiveData()
    val sceneState = appRepository.scenes
        .map { resultat ->
            resultat.fold(
                onSuccess = { if (it.isEmpty()) Empty else Normal },
                onFailure = { Error },
                onLoading = { Loading }
            )
        }
        .asLiveData()
    val info = appRepository.userInfo

    fun loadScene() {
        appRepository.refreshScenes()
    }

    fun loadDevice() {
        appRepository.refreshDevices()
    }

}