package com.github.miwu.logic.repository

import android.util.ArrayMap
import com.github.miwu.logic.handler.DeviceMetadataHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.putAll

class DeviceRepository : KoinComponent {
    private val specAttrProvider: MiotSpecAttrProvider by inject()
    private val iconMapMutex = Mutex()
    private val roomMapMutex = Mutex()
    private val handlerMutex = Mutex()
    private val iconMap = ArrayMap<String, String>()
    private val roomMap = ArrayMap<String, String>()
    private val _deviceMetadataHandler = MutableStateFlow(newHandler())
    val deviceMetadataHandler = _deviceMetadataHandler.asStateFlow()

    private suspend fun update() {
        handlerMutex.withLock {
            _deviceMetadataHandler.emit(newHandler())
        }
    }

    suspend fun addIcon(models: List<String>) = withContext(Dispatchers.IO) {
        models
            .mapNotNull { model ->
                if (model in iconMap) null
                else specAttrProvider.getIconUrl(model)
                    .getOrNull()
                    ?.takeIf(String::isNotEmpty)
                    ?.let { model to it }
            }
            .takeIf(List<*>::isNotEmpty)
            ?.let {
                iconMapMutex.withLock {
                    iconMap.putAll(it)
                }
                update()
            }
    }

    suspend fun addRoom(input: List<Pair<String, String>>) {
        input.takeIf(List<*>::isNotEmpty)
            ?.let {
                roomMapMutex.withLock {
                    roomMap.putAll(it)
                }
                update()
            }
    }

    fun getRoom(did: String): String = roomMap[did] ?: "未知位置" // TODO i18n

    fun getIcon(model: String): String? = iconMap[model]

    private fun newHandler() = DeviceMetadataHandler(iconMap.toMap(), roomMap.toMap())
}