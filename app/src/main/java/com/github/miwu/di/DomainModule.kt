package com.github.miwu.di

import com.github.miwu.domain.usecase.account.LoginUseCase
import com.github.miwu.domain.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.domain.usecase.device.ResolveDeviceSessionUseCase
import com.github.miwu.domain.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.domain.usecase.scene.GetHomeScenesUseCase
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val domainModule = module {
    factory<LoginUseCase>()
    factory<GetSortedDevicesUseCase>()
    factory<GetSortedRoomsUseCase>()
    factory<GetHomeScenesUseCase>()
    factory<ResolveDeviceSessionUseCase>()
}
