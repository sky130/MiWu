package com.github.miwu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditFavoriteViewModel : ViewModel() {

    val deviceFlow = DeviceRepository.flow

    suspend fun saveList(list: ArrayList<MiwuDevice>) = DeviceRepository.replaceList(list)

}