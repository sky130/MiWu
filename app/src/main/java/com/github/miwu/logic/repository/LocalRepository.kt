package com.github.miwu.logic.repository

import com.github.miwu.logic.database.entity.FavoriteDevice
import kotlinx.coroutines.flow.Flow
import miwu.miot.model.miot.MiotDevice
import java.util.concurrent.CopyOnWriteArrayList

interface LocalRepository {

    val deviceListFlow: Flow<List<FavoriteDevice>>

    val deviceList: List<FavoriteDevice>

    val iconMap: Map<String, ByteArray>

    fun addDevice(miotDevice: MiotDevice)

    fun removeDevice(favoriteDevice: FavoriteDevice)

    fun updateSortIndices(list: List<FavoriteDevice>)
}
