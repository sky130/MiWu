package com.github.miwu.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes

class MainViewModel : ViewModel() {
    val homes get() = AppRepository.homes
    val devices get() = AppRepository.devices
    val deviceList get() = AppRepository.deviceList
    val homeList get() = AppRepository.homeList


}