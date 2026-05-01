package com.github.miwu.logic.service

import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.service.DeviceTileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeviceTileRefreshService(
    private val localRepository: LocalRepository,
    private val scope: CoroutineScope,
) {
    init {
        localRepository.deviceListFlow
            .onEach { DeviceTileService.refresh() }
            .launchIn(scope)
    }
}
