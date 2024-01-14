package com.github.miwu.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.Miot
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotUserInfo

class MainViewModel : ViewModel() {
    val homes get() = AppRepository.homes
    val devices get() = AppRepository.devices
    val deviceList get() = AppRepository.deviceList
    val homeList get() = AppRepository.homeList
    val sceneList get() = AppRepository.sceneList
    val scenes get() = AppRepository.scenes

    val avatar = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val uid = MutableLiveData<String>()

    fun loadInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            miot.getUserInfo()?.let {
                avatar.postValue(it.info.avatar)
                nickname.postValue(it.info.nickname)
                uid.postValue(it.info.uid)
            }
        }
    }

}