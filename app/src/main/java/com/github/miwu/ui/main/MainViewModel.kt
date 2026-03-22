package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Error
import com.github.miwu.ui.main.state.FragmentState.Loading
import com.github.miwu.ui.main.state.FragmentState.Normal
import com.github.miwu.utils.Logger
import fr.haan.resultat.fold
import kotlinx.coroutines.flow.map


class MainViewModel(
    val miotRepository: MiotRepository,
    val cacheRepository: CacheRepository,
    val localRepository: LocalRepository,
    val dataStore: MiotUserDataStore
) : ViewModel() {
    private val logger = Logger()

    val info get() = miotRepository.user
    val home = miotRepository.currentHome
    val scenes = home.map { it.getOrNull()?.scenes.orEmpty() }.asLiveData()
    val devices = home.map { it.getOrNull()?.devices.orEmpty() }
        .map { device ->
            device.sortedWith(
                compareBy(
                    { !it.isOnline },
                    { cacheRepository.getRoom(it.did) },
                    { it.name.lowercase() }
                )
            )
        }.asLiveData()
    val metadataHandler = cacheRepository.deviceMetadataHandler
    val rooms = home.map {
        it.getOrNull()?.roomMap
            .orEmpty().values
            .sortedBy { it.name }
            .toList()
    }.asLiveData()
    val icons = cacheRepository.icons
    val localDeviceState = localRepository.deviceListFlow
        .map { if (it.isEmpty()) Empty else Normal }
        .asLiveData()
    val roomState = home.map { resultat ->
        resultat.fold(
            onSuccess = { if (it.rooms.isEmpty()) Empty else Normal },
            onFailure = { Error },
            onLoading = { Loading }
        )
    }.asLiveData()
    val deviceState = home.map { resultat ->
        resultat.fold(
            onSuccess = { if (it.devices.isEmpty()) Empty else Normal },
            onFailure = { Error },
            onLoading = { Loading }
        )
    }.asLiveData()
    val sceneState = home.map { resultat ->
        resultat.fold(
            onSuccess = { if (it.scenes.isEmpty()) Empty else Normal },
            onFailure = { Error },
            onLoading = { Loading }
        )
    }.asLiveData()
    val localDevices = localRepository.deviceListFlow
        .asLiveData()
//    val localDeviceState = localRepository.deviceListFlow
//        .map { if (it.isEmpty()) Empty else Normal }
//        .asLiveData()

    //    val scenes = appRepository.scenes
//        .map { it.getOrNull() ?: emptyList() }
//        .asLiveData()
//    val devices = appRepository.devices
//        .map { it.getOrNull() ?: emptyList() }
//        .map { device ->
//            device.sortedWith(
//                compareBy(
//                    { !it.isOnline },
//                    { deviceRepository.getRoom(it.did) },
//                    { it.name.lowercase() }
//                )
//            )
//        }
//        .asLiveData()
//    val deviceState = appRepository.devices
//        .map { resultat ->
//            resultat.fold(
//                onSuccess = { if (it.isEmpty()) Empty else Normal },
//                onFailure = { Error },
//                onLoading = { Loading }
//            )
//        }
//        .asLiveData()
//    val sceneState = appRepository.scenes
//        .map { resultat ->
//            resultat.fold(
//                onSuccess = { if (it.isEmpty()) Empty else Normal },
//                onFailure = { Error },
//                onLoading = { Loading }
//            )
//        }
//        .asLiveData()
//    val info = appRepository.userInfo

    fun loadScene() {
        miotRepository.refreshCurrentHome()
    }

    fun loadDevice() {
        miotRepository.refreshCurrentHome()
    }

    fun loadRoom() {
        miotRepository.refreshCurrentHome()
    }

}