package com.github.miwu.logic.usecase.home

import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.entity.MiotHomeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotRoom
import miwu.miot.model.miot.MiotScene

class ConvertHomeDataUseCase(
    private val cacheRepository: CacheRepository,
) {
    private val sceneMutex = Mutex()
    private val sceneMap = mutableMapOf<MiotScene, MiotHome>()

    suspend operator fun invoke(
        client: MiotHomeClient,
        homes: List<MiotHome>,
    ): List<Pair<String, MiotHomeData>> = withContext(Dispatchers.IO) {
        homes.map { home -> async { convertToData(client, home)?.let { home.id to it } } }
            .awaitAll()
            .filterNotNull()
    }

    suspend operator fun invoke(
        client: MiotHomeClient,
        home: MiotHome,
    ): MiotHomeData? = convertToData(client, home)

    private suspend fun convertToData(client: MiotHomeClient, home: MiotHome): MiotHomeData? {
        val devices = getDevices(client, home).sortedBy(MiotDevice::name)
        val deviceMap = devices.associateBy(MiotDevice::did)

        devices.associateBy(MiotDevice::did) { device ->
            home.rooms.firstOrNull { device.did in it.dids }?.name ?: "未知"
        }.let { cacheRepository.addRoom(it) }

        return MiotHomeData(
            home = home,
            rooms = home.rooms.associateBy(MiotRoom::name) {
                it.dids
                    .mapNotNull(deviceMap::get)
                    .sortedBy(MiotDevice::name)
            },
            scenes = client.getScenes(home)
                .getOrNull()
                ?.result
                ?.scenes
                ?.sortedBy(MiotScene::name)
                .orEmpty().also { scenes ->
                    sceneMutex.withLock {
                        sceneMap += scenes.associateBy({ it }) { home }
                    }
                },
            devices = devices,
            roomMap = home.rooms.associateBy(MiotRoom::name)
        )
    }

    private suspend fun getDevices(client: MiotHomeClient, home: MiotHome) =
        client.getDevices(home.id.toLong(), home.uid)
            .onFailure {
                it.printStackTrace()
            }
            .getOrNull()
            ?.result
            ?.deviceInfo
            ?.also { cacheRepository.addIcon(it.map(MiotDevice::model)) }
            .orEmpty()

    fun getSceneHome(scene: MiotScene): MiotHome? = sceneMap[scene]
}
