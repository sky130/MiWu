package com.github.miwu.logic.repository

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.preferences.AppPreferences
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes

object AppRepository {
    private val job = Job()
    private val scope = CoroutineScope(job)
    val deviceList = ObservableArrayList<MiotDevices.Result.Device>()
    val homeList = ObservableArrayList<MiotHomes.Result.Home>()
    val homes = MutableLiveData<MiotHomes>()
    val devices = MutableLiveData<MiotDevices>()

    fun loadHomes() {
        scope.launch(Dispatchers.IO) {
            miot.getHomes().also {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        "加载家庭失败".toast()
                    } else {
                        homeList.clear()
                        homeList.addAll(it.result.homes)
                        homes.value = it
                    }
                }
            }?.let {
                if (AppPreferences.homeId == 0L) {
                    it.result.homes.firstNotNullOf { home ->
                        AppPreferences.homeId = home.id.toLong()
                        AppPreferences.homeUid = home.uid
                    }
                    loadDevice()
                }
            }
        }
    }

    fun loadDevice() {
        if (AppPreferences.homeId == 0L) return
        scope.launch(Dispatchers.IO) {
            miot.getDevices(AppPreferences.homeUid, AppPreferences.homeId)?.also {
                it.log.d()
            }.let {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        "加载设备失败".toast()
                    } else {
                        deviceList.clear()
                        deviceList.addAll(it.result.deviceInfo)
                        devices.value = it
                    }
                }
            }
        }
    }
}