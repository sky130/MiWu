package com.github.miwu.logic.usecase.scene

import com.github.miwu.logic.repository.MiotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotScene

class GetHomeScenesUseCase(
    private val miotRepository: MiotRepository,
) {
    operator fun invoke(): Flow<List<MiotScene>> {
        return miotRepository.currentHome.map {
            it.getOrNull()?.scenes.orEmpty()
        }
    }
}
