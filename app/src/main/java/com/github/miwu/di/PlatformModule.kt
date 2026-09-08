package com.github.miwu.di

import com.github.miwu.domain.gateway.DeviceIdProvider
import com.github.miwu.domain.repository.CrashLogRepository
import com.github.miwu.platform.crash.CrashHandler
import com.github.miwu.platform.device.AndroidDeviceIdProvider
import com.github.miwu.platform.tile.DeviceTileRefreshCoordinator
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val platformModule = module {
    single<AndroidDeviceIdProvider>().bind<DeviceIdProvider>()
    single<CrashHandler>().bind<CrashLogRepository>()
    single<DeviceTileRefreshCoordinator>().withOptions { createdAtStart() }
}
