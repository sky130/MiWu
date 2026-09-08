package com.github.miwu.di

import com.github.miwu.domain.usecase.account.LoginUseCase
import com.github.miwu.domain.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.domain.usecase.device.ResolveDeviceSessionUseCase
import com.github.miwu.domain.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.domain.usecase.scene.GetHomeScenesUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get(), get(), get(appIoDispatcher)) }
    factory { GetSortedDevicesUseCase(get(), get()) }
    factory { GetSortedRoomsUseCase(get()) }
    factory { GetHomeScenesUseCase(get()) }
    factory { ResolveDeviceSessionUseCase(get(), get(), get()) }
}
