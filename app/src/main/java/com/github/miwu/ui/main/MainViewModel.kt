package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.utils.Logger
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.ui.main.state.FragmentState.*
import fr.haan.resultat.fold
import fr.haan.resultat.onFailure
import fr.haan.resultat.onSuccess
import kndroidx.extension.toast
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotUserInfo
import kotlin.collections.sortedWith

class MainViewModel(
    val appRepository: AppRepository,
    val localRepository: LocalRepository,
    val deviceRepository: DeviceRepository,
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
    val info = flow {
        runCatching {
            appRepository.getUserInfo().getOrThrow()
        }.onSuccess {
            emit(it.info)
        }.onFailure {
            it.message?.toast()
            logger.error("get user info failed, {}", it.message)
            it.printStackTrace()
            emit(MiotUserInfo.UserInfo(0L, "", "null"))
        }
    }.asLiveData()

    fun init() {
        appRepository.loadAll()
    }

    fun loadScene() {
        appRepository.loadScenes()
    }

    fun loadDevice() {
        appRepository.loadDevices()
    }

}