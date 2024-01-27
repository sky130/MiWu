package com.github.miwu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val deviceList get() = AppRepository.deviceList
    val sceneList get() = AppRepository.sceneList

    val deviceFlow = DeviceRepository.flow
    var homeId = AppPreferences.homeId
    val avatar = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val uid = MutableLiveData<String>()

    fun saveList(list: ArrayList<MiwuDevice>) {
        viewModelScope.launch {
            DeviceRepository.replaceList(list)
        }
    }

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