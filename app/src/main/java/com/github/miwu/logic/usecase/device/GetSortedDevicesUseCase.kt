package com.github.miwu.logic.usecase.device

import com.github.miwu.BuildConfig
import com.github.miwu.mock.GeneratedMockDevices
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotDevice

class GetSortedDevicesUseCase(
    private val miotRepository: MiotRepository,
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): Flow<List<MiotDevice>> {
        return miotRepository.currentHome.map { resultat ->
            val mockDevices = loadMockDevices()

            (resultat.getOrNull()?.devices.orEmpty() + mockDevices)
                .sortedWith(
                    compareBy(
                        { !it.isOnline },
                        { cacheRepository.getRoom(it.did) },
                        { it.name.lowercase() }
                    )
                )
        }
    }

    operator fun invoke(roomName: String): Flow<List<MiotDevice>> {
        return miotRepository.currentHome.map { resultat ->
            val mockDevices = loadMockDevices()
                .filter { GeneratedMockDevices.rooms[it.did] == roomName }

            (resultat.getOrNull()?.rooms?.get(roomName).orEmpty() + mockDevices)
                .sortedWith(
                    compareBy(
                        { !it.isOnline },
                        { cacheRepository.getRoom(it.did) },
                        { it.name.lowercase() }
                    )
                )
        }
    }

    private suspend fun loadMockDevices(): List<MiotDevice> {
        if (!BuildConfig.DEBUG || !GeneratedMockDevices.enabled) return emptyList()

        val devices = GeneratedMockDevices.devices
        if (devices.isEmpty()) return emptyList()

        cacheRepository.addIcon(devices.map(MiotDevice::model))
        cacheRepository.addRoom(GeneratedMockDevices.rooms)
        return devices
    }
}
