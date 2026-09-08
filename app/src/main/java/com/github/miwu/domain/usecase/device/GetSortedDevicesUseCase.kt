package com.github.miwu.domain.usecase.device

import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import miwu.miot.model.miot.MiotDevice
import org.koin.core.annotation.Factory

@Factory
class GetSortedDevicesUseCase(
    private val homeRepository: HomeRepository,
    private val metadataRepository: DeviceMetadataRepository,
) {
    operator fun invoke(roomName: String? = null): Flow<List<MiotDevice>> =
        combine(homeRepository.currentHome, metadataRepository.metadata) { result, metadata ->
            val home = result.getOrNull()
            val devices = if (roomName == null) {
                home?.devices.orEmpty()
            } else {
                home?.rooms?.get(roomName).orEmpty()
            }
            devices.sortedWith(
                compareBy(
                    { !it.isOnline },
                    { metadata.getRoom(it.did) },
                    { it.name.lowercase() },
                )
            )
        }
}
