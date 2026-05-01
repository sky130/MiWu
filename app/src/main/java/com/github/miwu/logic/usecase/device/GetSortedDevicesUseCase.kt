package com.github.miwu.logic.usecase.device

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
            resultat.getOrNull()
                ?.devices
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
