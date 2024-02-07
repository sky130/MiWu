package com.github.miwu.viewmodel

import android.util.ArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceArrayList
import com.github.miwu.logic.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes

class SmartViewModel : ViewModel() {

    val uid = MutableLiveData<String>()
    val list get() = AppRepository.smartFlow
    val refresh get() = AppRepository.smartRefreshFlow

}