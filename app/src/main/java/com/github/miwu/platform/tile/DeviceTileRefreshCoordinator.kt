package com.github.miwu.platform.tile

import com.github.miwu.di.AppScope
import com.github.miwu.domain.repository.DeviceIconRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class DeviceTileRefreshCoordinator(
    favoriteDeviceRepository: FavoriteDeviceRepository,
    deviceIconRepository: DeviceIconRepository,
    @AppScope applicationScope: CoroutineScope,
) {
    init {
        combine(favoriteDeviceRepository.devices, deviceIconRepository.icons) { _, _ -> Unit }
            .onEach { DeviceTileService.refresh() }
            .launchIn(applicationScope)
    }
}
