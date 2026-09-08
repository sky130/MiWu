package com.github.miwu.data.favorite

import com.github.miwu.data.local.database.AppDatabase
import com.github.miwu.data.local.database.entity.FavoriteDeviceEntity.Companion.toEntity
import com.github.miwu.data.local.database.entity.FavoriteDeviceEntity.Companion.toMiot
import com.github.miwu.data.local.database.entity.FavoriteDeviceOrderEntity
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import miwu.miot.model.miot.MiotDevice

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteDeviceRepositoryImpl(
    database: AppDatabase,
    private val accountRepository: AccountRepository,
    private val metadataRepository: DeviceMetadataRepository,
    private val ioDispatcher: CoroutineDispatcher,
    applicationScope: CoroutineScope,
) : FavoriteDeviceRepository {
    private val dao = database.favoriteDeviceDao()

    override val devices: StateFlow<List<MiotDevice>> = accountRepository.loginState
        .flatMapLatest { state ->
            val uid = accountRepository.currentUser?.userId?.toLongOrNull()
            if (state is LoginState.Success && uid != null) dao.observeList(uid) else flowOf(emptyList())
        }
        .map { entities -> entities.map { it.toMiot() } }
        .onEach { devices -> metadataRepository.ensureIcons(devices.map(MiotDevice::model)) }
        .stateIn(applicationScope, SharingStarted.Eagerly, emptyList())

    override suspend fun add(device: MiotDevice) = withContext(ioDispatcher) {
        val uid = accountRepository.currentUser?.userId?.toLongOrNull() ?: return@withContext
        dao.insert(device.toEntity(uid))
        dao.insertOrder(FavoriteDeviceOrderEntity(uid, device.did))
        updateOrderEntities(uid, dao.getList(uid).map { it.did })
    }

    override suspend fun remove(device: MiotDevice) = withContext(ioDispatcher) {
        val uid = accountRepository.currentUser?.userId?.toLongOrNull() ?: device.uid
        dao.delete(device.toEntity(uid))
    }

    override suspend fun updateOrder(devices: List<MiotDevice>) = withContext(ioDispatcher) {
        val uid = accountRepository.currentUser?.userId?.toLongOrNull() ?: return@withContext
        updateOrderEntities(uid, devices.map(MiotDevice::did))
    }

    override fun find(did: String, uid: Long): MiotDevice? =
        devices.value.firstOrNull { it.did == did && it.uid == uid }

    private suspend fun updateOrderEntities(uid: Long, dids: List<String>) {
        dids.mapIndexed { index, did -> FavoriteDeviceOrderEntity(uid, did, index) }
            .let { dao.updateSortIndices(it) }
    }
}
