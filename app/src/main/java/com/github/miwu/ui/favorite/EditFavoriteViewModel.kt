package com.github.miwu.ui.favorite

import androidx.lifecycle.ViewModel
import com.github.miwu.logic.database.model.MiwuDatabaseDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository

class EditFavoriteViewModel(val devicesRepository: DeviceRepository, val appRepository: AppRepository) : ViewModel() {

    val deviceFlow = devicesRepository.deviceList

    suspend fun saveList(list: ArrayList<MiwuDatabaseDevice>) = devicesRepository.replaceList(list)

}