package com.github.miwu.logic.usecase.device

import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotDevice
import miwu.support.mock.MockMiotDevice

class GetSortedDevicesUseCase(
    private val miotRepository: MiotRepository,
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): Flow<List<MiotDevice>> {
        return miotRepository.currentHome.map { resultat ->
            resultat.getOrNull()
                ?.devices
                .orEmpty()
                .toMutableList()
                .apply {
                    add(
                        MockMiotDevice(
                            name = "NWT智能除湿机23L",
                            did = "012345689abcdef",
                            model = "nwt.derh.n23l",
                            specType = "urn:miot-spec-v2:device:dehumidifier:0000A02D:nwt-n23l:1:0000D025"
                        )
                    )
                }
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
            resultat.getOrNull()
                ?.rooms?.get(roomName)
                .orEmpty()
                .sortedWith(
                    compareBy(
                        { !it.isOnline },
                        { cacheRepository.getRoom(it.did) },
                        { it.name.lowercase() }
                    )
                )
        }
    }

}
