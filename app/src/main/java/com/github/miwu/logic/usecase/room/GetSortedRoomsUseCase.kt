package com.github.miwu.logic.usecase.room

import com.github.miwu.logic.repository.MiotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotRoom

class GetSortedRoomsUseCase(
    private val miotRepository: MiotRepository,
) {
    operator fun invoke(): Flow<List<MiotRoom>> {
        return miotRepository.currentHome.map { resultat ->
            resultat.getOrNull()
                ?.roomMap
                .orEmpty()
                .values
                .sortedBy { it.name }
                .toList()
        }
    }
}
