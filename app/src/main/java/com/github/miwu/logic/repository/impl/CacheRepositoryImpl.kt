package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.handler.DeviceMetadataHandler
import com.github.miwu.logic.repository.CacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CacheRepositoryImpl : KoinComponent, CacheRepository {
    private val specAttrProvider: MiotSpecAttrProvider by inject()
    private val iconMap = mutableMapOf<String, String>()
    private val roomMap = mutableMapOf<String, String>()
    private val iconMutex = Mutex()
    private val roomMutex = Mutex()
    override val icons = MutableStateFlow<Map<String, String>>(emptyMap())
    override val rooms = MutableStateFlow<Map<String, String>>(emptyMap())
    override val deviceMetadataHandler = MutableStateFlow(DeviceMetadataHandler(iconMap, roomMap))

    override suspend fun addIcon(models: List<String>): Unit = withContext(Dispatchers.IO) {
        models
            .mapNotNull { model ->
                if (model in iconMap) null
                else specAttrProvider.getIconUrl(model)
                    .getOrNull()
                    ?.takeIf(String::isNotEmpty)
                    ?.let { model to it }
            }
            .takeIf(List<*>::isNotEmpty)
            ?.also {
                iconMutex.withLock {
                    iconMap.putAll(it)
                    icons.emit(iconMap)
                }
            }
        update()
    }

    override suspend fun addRoom(map: Map<String, String>) {
        roomMutex.withLock {
            roomMap.putAll(map)
            rooms.emit(roomMap)
        }
        update()
    }


    override fun getIcon(model: String) = iconMap[model]
    override fun getRoom(did: String): String = roomMap[did] ?: "未知位置" // TODO i18n

    private suspend fun update() {
        val icon = iconMap.toMap()
        val room = roomMap.toMap()
        deviceMetadataHandler.emit(DeviceMetadataHandler(icon, room))
        icons.emit(icon)
        rooms.emit(room)
    }
}