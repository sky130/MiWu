package com.github.miwu.domain.usecase.scene

import com.github.miwu.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotScene
import org.koin.core.annotation.Factory

@Factory
class GetHomeScenesUseCase(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(): Flow<List<MiotScene>> =
        homeRepository.currentHome.map { it.getOrNull()?.scenes.orEmpty() }
}
