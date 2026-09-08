package com.github.miwu.platform.tile

import com.github.miwu.domain.repository.DeviceIconRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeviceTileRefreshCoordinator(
    favoriteDeviceRepository: FavoriteDeviceRepository,
    deviceIconRepository: DeviceIconRepository,
    applicationScope: CoroutineScope,
) {
    init {
        combine(favoriteDeviceRepository.devices, deviceIconRepository.icons) { _, _ -> Unit }
            .onEach { DeviceTileService.refresh() }
            .launchIn(applicationScope)
    }
}
