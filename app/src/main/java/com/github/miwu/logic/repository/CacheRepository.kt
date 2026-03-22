package com.github.miwu.logic.repository

import com.github.miwu.logic.handler.DeviceMetadataHandler
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.miot.MiotDevice

interface CacheRepository {
    val icons: StateFlow<Map<String, String>>
    val rooms: StateFlow<Map<String, String>>
    val deviceMetadataHandler: StateFlow<DeviceMetadataHandler>

    suspend fun addIcon(models: List<String>)

    suspend fun addRoom(map: Map<String, String>)

    fun getIcon(model: String): String?

    fun getRoom(did: String): String
}