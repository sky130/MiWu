package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.auth.AuthService
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiot
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiwu
import com.github.miwu.logic.database.entity.FavoriteDeviceMetadata
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice
import com.github.miwu.logic.state.LoginState
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LocalRepositoryImpl(
    private val scope: CoroutineScope,
    private val database: AppDatabase,
    private val cacheRepository: CacheRepository,
    private val authService: AuthService,
) : LocalRepository {
    private val dao get() = database.deviceDAO()
    private val deviceMetadataHandler = cacheRepository.deviceMetadataHandler
    private val httpClient = HttpClient()
    private val logger = Logger()
    private val uid get() = authService.getCurrentUser()?.userId?.toLongOrNull()
    override val deviceList = CopyOnWriteArrayList<FavoriteDevice>()
    override val iconMap = mutableMapOf<String, ByteArray>()
    override val deviceListFlow: Flow<List<FavoriteDevice>> =
        authService.loginStatus
            .flatMapLatest { state ->
                val uid = uid
                if (state is LoginState.Success && uid != null) dao.observeList(uid)
                else flowOf(emptyList())
            }.onEach {
                cacheRepository.addIcon(it.map(FavoriteDevice::model))
                deviceList.clear()
                deviceList.addAll(it)
                refreshIcon()
            }

    init {
        deviceMetadataHandler.onEach { refreshIcon() }.launchIn(scope)
        deviceListFlow.launchIn(scope)
    }

    override fun addDevice(miotDevice: MiotDevice) {
        scope.launch {
            val uid = uid ?: return@launch
            dao.insert(miotDevice.toMiwu(uid))
            dao.insertMeta(FavoriteDeviceMetadata(uid, miotDevice.did))
            updateSortIndices(dao.getList(uid))
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

    override fun findDevice(did: String, uid: Long): MiotDevice? =
        deviceList.firstOrNull { it.did == did && it.uid == uid }?.toMiot()

    private suspend fun refreshIcon() {
        val handler = deviceMetadataHandler.value
        logger.info("refreshIcon")
        deviceList
            .map(FavoriteDevice::model)
            .forEach { model ->
                if (iconMap[model] != null) {
                    logger.info("model: {}, has icon cache, skip", model)
                    return@forEach
                }
                logger.info("model: {}, load icon", model)
                handler.getIcon(model)
                    ?.let { httpClient.get(it) }
                    ?.bodyAsBytes()
                    ?.also { iconMap[model] = it }
                    ?: logger.info("model: {}, load icon failure", model)
            }
    }

    private fun convert(list: List<FavoriteDevice>) = list.map { it.toMiot() }
}
