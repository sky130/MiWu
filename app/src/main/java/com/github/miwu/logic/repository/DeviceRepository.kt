package com.github.miwu.logic.repository

import android.util.ArrayMap
import com.github.miwu.logic.handler.DeviceMetadataHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.putAll

interface DeviceRepository {
    val deviceMetadataHandler: StateFlow<DeviceMetadataHandler>

    suspend fun addIcon(models: List<String>)

    suspend fun addRoom(input: List<Pair<String, String>>)

    fun getRoom(did: String): String

    fun getIcon(model: String): String?
}