package com.github.miwu.domain.repository

import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.miot.MiotDevice

interface FavoriteDeviceRepository {
    val devices: StateFlow<List<MiotDevice>>

    suspend fun add(device: MiotDevice)

    suspend fun remove(device: MiotDevice)

    suspend fun updateOrder(devices: List<MiotDevice>)

    fun find(did: String, uid: Long): MiotDevice?
}
