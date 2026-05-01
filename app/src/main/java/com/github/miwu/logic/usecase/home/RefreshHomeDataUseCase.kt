package com.github.miwu.logic.usecase.home

import com.github.miwu.logic.repository.MiotRepository

class RefreshHomeDataUseCase(
    private val miotRepository: MiotRepository,
) {
    operator fun invoke() {
        miotRepository.refreshCurrentHome()
    }
}
