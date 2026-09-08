package com.github.miwu.domain.repository

import com.github.miwu.domain.model.DeviceMetadata
import kotlinx.coroutines.flow.StateFlow

interface DeviceMetadataRepository {
    val metadata: StateFlow<DeviceMetadata>

    suspend fun ensureIcons(models: Collection<String>)

    suspend fun updateRooms(rooms: Map<String, String>)

    suspend fun clear()
}
