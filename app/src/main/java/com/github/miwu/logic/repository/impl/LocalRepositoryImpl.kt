package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiot
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiwu
import com.github.miwu.logic.database.entity.FavoriteDeviceMetadata
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.service.DeviceTileService
import com.github.miwu.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kndroidx.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CopyOnWriteArrayList

class LocalRepositoryImpl : KoinComponent, LocalRepository {
    private val scope: CoroutineScope by inject()
    private val database: AppDatabase by inject()
    private val dao get() = database.deviceDAO()
    private val deviceRepository: DeviceRepository by inject()
    private val deviceMetadataHandler = deviceRepository.deviceMetadataHandler
    private val httpClient = HttpClient()
    private val logger = Logger()

    init {
        deviceMetadataHandler.onEach {
            refreshIcon()
        }.launchIn(scope)
    }

    override val deviceList = CopyOnWriteArrayList<FavoriteDevice>()
    override val iconMap = mutableMapOf<String, ByteArray>()
    override val deviceListFlow: Flow<List<FavoriteDevice>> = dao.observeList()
        .onEach { deviceRepository.addIcon(it.map(FavoriteDevice::model)) }
        .onEach {
            deviceList.clear()
            deviceList.addAll(it)
            refreshIcon()
            DeviceTileService.refresh()
        }

    override fun addDevice(miotDevice: MiotDevice) {
        scope.launch {
            dao.insert(miotDevice.toMiwu())
            dao.insertMeta(FavoriteDeviceMetadata(miotDevice.uid, miotDevice.did))
            updateSortIndices(dao.getList())
        }
    }

    override fun removeDevice(favoriteDevice: FavoriteDevice) {
        scope.launch {
            dao.delete(favoriteDevice)
        }
    }

    override fun updateSortIndices(list: List<FavoriteDevice>) {
        scope.launch {
            list
                .mapIndexed { index, item ->
                    FavoriteDeviceMetadata(item.uid, item.did, index)
                }
                .let { dao.updateSortIndices(it) }
        }
    }

    private suspend fun refreshIcon() {
        val handler = deviceMetadataHandler.value
        logger.info("refreshIcon")
        deviceList.take(4).forEach { device ->
            val model = device.model
            logger.info("model: {}", model)
            if (iconMap[model] == null) {
                handler.getIcon(model)
                    ?.let { httpClient.get(it) }
                    ?.bodyAsBytes()
                    ?.also { iconMap[model] = it }
                    ?: logger.info("model: {}, load icon failure", model)
            }
        }
    }

    private fun convert(list: List<FavoriteDevice>) = list.map { it.toMiot() }
}
