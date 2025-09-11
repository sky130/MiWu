package com.github.miwu.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.ui.main.FragmentState.*
import fr.haan.resultat.onFailure
import fr.haan.resultat.onLoading
import fr.haan.resultat.onSuccess
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotUserInfo
import kotlin.collections.sorted
import kotlin.collections.sortedWith

class MainViewModel(val appRepository: AppRepository, val deviceRepository: DeviceRepository) :
    ViewModel() {
    val scenes = appRepository.scenes
        .map { it.getOrNull() ?: emptyList() }
        .asLiveData()
    val devices = appRepository.devices
        .map { it.getOrNull() ?: emptyList() }
        .map {
            it.sortedWith(
                compareBy(
                    { !it.isOnline },
                    { getRoomName(it) },
                    { it.name.lowercase() }
                )
            )
        }
        .asLiveData()
    val deviceState = appRepository.devices
        .map {
            it.onSuccess { devices ->
                return@map if (devices.isEmpty()) Empty else Normal
            }.onFailure {
                return@map Error
            }
            return@map Loading
        }.asLiveData()
    val sceneState = appRepository.scenes
        .map {
            it.onSuccess { scenes ->
                return@map if (scenes.isEmpty()) Empty else Normal
            }.onFailure {
                return@map Error
            }
            return@map Loading
        }.asLiveData()
    val info = flow {
        runCatching {
            appRepository.miotClient.getUserInfo()
        }.onSuccess {
            emit(it.info)
        }.onFailure {
            emit(MiotUserInfo.UserInfo("null", "", "null"))
        }
    }.asLiveData()

    fun getRoomName(item: MiotDevices.Result.Device) =
        appRepository.getRoomName(item as MiotDevices.Result.Device)

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