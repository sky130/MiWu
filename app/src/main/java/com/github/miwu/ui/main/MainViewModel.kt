package com.github.miwu.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.ui.main.state.FragmentState.*
import com.github.miwu.utils.Logger
import fr.haan.resultat.fold
import kndroidx.extension.toast
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotUserInfo
import java.util.TreeMap
import java.util.function.Function
import kotlin.math.min


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

    fun loadScene() {
        appRepository.refreshScenes()
    }

    fun loadDevice() {
        appRepository.refreshDevices()
    }

}