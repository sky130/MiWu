package com.github.miwu.domain.usecase.room

import com.github.miwu.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotRoom

class GetSortedRoomsUseCase(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(): Flow<List<MiotRoom>> =
        homeRepository.currentHome.map { result ->
            result.getOrNull()
                ?.roomMap
                .orEmpty()
                .values
                .sortedBy(MiotRoom::name)
        }
}
