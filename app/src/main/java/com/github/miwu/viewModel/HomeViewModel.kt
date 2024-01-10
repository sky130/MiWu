package com.github.miwu.viewmodel

import androidx.lifecycle.ViewModel
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import miot.kotlin.model.miot.MiotHomes

class HomeViewModel : ViewModel() {
    val homes get() = AppRepository.homes
    val devices get() = AppRepository.devices
    val deviceList get() = AppRepository.deviceList
    val homeList get() = AppRepository.homeList

    fun isHome(item: MiotHomes.Result.Home) = item.id.toLong() == AppPreferences.homeId

}