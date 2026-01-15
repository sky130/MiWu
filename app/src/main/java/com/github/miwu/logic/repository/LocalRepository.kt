package com.github.miwu.logic.repository

import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiot
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiwu
import com.github.miwu.logic.database.entity.FavoriteDeviceMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice
import miwu.support.base.MiwuDevice
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalRepository : KoinComponent {
    private val scope: CoroutineScope by inject()
    private val database: AppDatabase by inject()
    private val dao get() = database.deviceDAO()
    private val deviceRepository: DeviceRepository by inject()

    val deviceList: Flow<List<FavoriteDevice>> = dao.observeList()
        .onEach { deviceRepository.addIcon(it.map(FavoriteDevice::model)) }

//    val deviceList: Flow<List<MiotDevice>> = originDeviceList
//        .map(::convert)

    fun addDevice(miotDevice: MiotDevice) {
        scope.launch {
            dao.insert(miotDevice.toMiwu())
            dao.insertMeta(FavoriteDeviceMetadata(miotDevice.uid, miotDevice.did))
            updateSortIndices(dao.getList())
        }
    }

    fun updateSortIndices(list: List<FavoriteDevice>) {
        scope.launch {
            list.mapIndexed { index, item ->
                FavoriteDeviceMetadata(item.uid, item.did, index)
            }.let { dao.updateSortIndices(it) }
        }
    }

    private fun convert(list: List<FavoriteDevice>) =
        list.map { it.toMiot() }
}
