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
                            name = MOCK_DEVICE_NAME,
                            did = MOCK_DEVICE_DID,
                            model = MOCK_DEVICE_MODEL,
                            specType = MOCK_DEVICE_SPEC_TYPE
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

    companion object {
        private const val MOCK_DEVICE_NAME = "Mock 窗帘"
        private const val MOCK_DEVICE_DID = "abcdef123456"
        private const val MOCK_DEVICE_MODEL = "cmjd.curtain.cmx82"
        private const val MOCK_DEVICE_SPEC_TYPE =
            "urn:miot-spec-v2:device:curtain:0000A00C:cmjd-cmx82:1:0000D031"
    }
}
