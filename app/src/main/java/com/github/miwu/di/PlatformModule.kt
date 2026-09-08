package com.github.miwu.di

import com.github.miwu.domain.gateway.DeviceIdProvider
import com.github.miwu.domain.repository.CrashLogRepository
import com.github.miwu.platform.crash.CrashHandler
import com.github.miwu.platform.device.AndroidDeviceIdProvider
import com.github.miwu.platform.tile.DeviceTileRefreshCoordinator
import org.koin.dsl.module

val platformModule = module {
    single<DeviceIdProvider> { AndroidDeviceIdProvider(get()) }
    single { CrashHandler(get(), get(), get(appIoDispatcher)) }
    single<CrashLogRepository> { get<CrashHandler>() }
    single(createdAtStart = true) { DeviceTileRefreshCoordinator(get(), get(), get(appScope)) }
}
