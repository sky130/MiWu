package com.github.miwu.logic.device

import com.github.miwu.BuildConfig
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.mock.GeneratedMockDevices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import miwu.miot.model.miot.MiotDevice

interface DeviceCatalog {
    val devices: Flow<List<MiotDevice>>

    fun findByDid(did: String): MiotDevice?

    fun roomFor(did: String): String?
}

class DeviceCatalogImpl(
    private val miotRepository: MiotRepository,
    private val cacheRepository: CacheRepository,
) : DeviceCatalog {
    private val mockDevices = if (BuildConfig.DEBUG && GeneratedMockDevices.enabled) {
        GeneratedMockDevices.devices
    } else {
        emptyList()
    }
    private val mockRooms = if (mockDevices.isEmpty()) {
        emptyMap()
    } else {
        GeneratedMockDevices.rooms
    }
    private val metadataMutex = Mutex()
    private var metadataLoaded = false

    override val devices: Flow<List<MiotDevice>> = miotRepository.currentHome.map { result ->
        ensureMockMetadata()
        merge(result.getOrNull()?.devices.orEmpty(), mockDevices)
    }

    override fun findByDid(did: String): MiotDevice? {
        val currentDevice = miotRepository.currentHome.value
            .getOrNull()
            ?.devices
            ?.firstOrNull { it.did == did }
        return currentDevice ?: mockDevices.firstOrNull { it.did == did }
    }

    override fun roomFor(did: String): String? {
        // A real device wins when a configured Mock accidentally shares its did.
        val isRealDevice = miotRepository.currentHome.value
            .getOrNull()
            ?.devices
            ?.any { it.did == did } == true
        return if (isRealDevice) cacheRepository.getRoom(did) else {
            mockRooms[did] ?: cacheRepository.getRoom(did)
        }
    }

    private suspend fun ensureMockMetadata() {
        if (metadataLoaded || mockDevices.isEmpty()) return
        metadataMutex.withLock {
            if (metadataLoaded) return
            cacheRepository.addIcon(mockDevices.map(MiotDevice::model))
            cacheRepository.addRoom(mockRooms)
            metadataLoaded = true
        }
    }

    private fun merge(realDevices: List<MiotDevice>, mockDevices: List<MiotDevice>): List<MiotDevice> {
        return (realDevices + mockDevices).distinctBy(MiotDevice::did)
    }
}
