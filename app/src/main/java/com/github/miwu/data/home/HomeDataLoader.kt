package com.github.miwu.data.home

import com.github.miwu.BuildConfig
import com.github.miwu.domain.model.HomeData
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.mock.GeneratedMockDevices
import com.github.miwu.utils.throwIfCancelled
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotRoom
import miwu.miot.model.miot.MiotScene
import java.util.concurrent.ConcurrentHashMap

class HomeDataLoader(
    private val metadataRepository: DeviceMetadataRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val sceneHomes = ConcurrentHashMap<MiotScene, MiotHome>()

    suspend fun load(
        client: MiotHomeClient,
        homes: List<MiotHome>,
    ): Map<String, HomeData> = withContext(ioDispatcher) {
        coroutineScope {
            homes.map { home -> async { load(client, home)?.let { home.id to it } } }
                .awaitAll()
                .filterNotNull()
                .toMap()
        }
    }

    suspend fun load(client: MiotHomeClient, home: MiotHome): HomeData? =
        withContext(ioDispatcher) { loadHome(client, home) }

    fun getSceneHome(scene: MiotScene): MiotHome? = sceneHomes[scene]

    fun clear() {
        sceneHomes.clear()
    }

    private suspend fun loadHome(client: MiotHomeClient, home: MiotHome): HomeData {
        val remoteDevices = getRemoteDevices(client, home)
        val mockDevices = getMockDevices()
        val devices = (remoteDevices + mockDevices).distinctBy(MiotDevice::did)
        val deviceById = devices.associateBy(MiotDevice::did)
        val roomByDevice = buildMap {
            remoteDevices.forEach { device ->
                put(device.did, home.rooms.firstOrNull { device.did in it.dids }?.name ?: UNKNOWN_ROOM)
            }
            putAll(getMockRooms())
        }

        metadataRepository.updateRooms(roomByDevice)
        metadataRepository.ensureIcons(devices.map(MiotDevice::model))

        val scenes = client.getScenes(home)
            .throwIfCancelled()
            .getOrNull()
            ?.result
            ?.scenes
            ?.sortedBy(MiotScene::name)
            .orEmpty()
        sceneHomes.putAll(scenes.associateWith { home })

        return HomeData(
            home = home,
            rooms = home.rooms.associateBy(MiotRoom::name) { room ->
                val remoteRoomDevices = room.dids.mapNotNull(deviceById::get)
                val mockRoomDevices = mockDevices.filter { getMockRooms()[it.did] == room.name }
                (remoteRoomDevices + mockRoomDevices)
                    .distinctBy(MiotDevice::did)
                    .sortedBy(MiotDevice::name)
            },
            scenes = scenes,
            devices = devices.sortedBy(MiotDevice::name),
            roomMap = home.rooms.associateBy(MiotRoom::name),
        )
    }

    private suspend fun getRemoteDevices(client: MiotHomeClient, home: MiotHome): List<MiotDevice> =
        client.getDevices(home.id.toLong(), home.uid)
            .throwIfCancelled()
            .getOrNull()
            ?.result
            ?.deviceInfo
            .orEmpty()

    private fun getMockDevices(): List<MiotDevice> =
        if (BuildConfig.DEBUG && GeneratedMockDevices.enabled) GeneratedMockDevices.devices else emptyList()

    private fun getMockRooms(): Map<String, String> =
        if (BuildConfig.DEBUG && GeneratedMockDevices.enabled) GeneratedMockDevices.rooms else emptyMap()

    private companion object {
        const val UNKNOWN_ROOM = "未知"
    }
}
