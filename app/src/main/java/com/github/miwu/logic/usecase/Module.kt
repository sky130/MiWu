package com.github.miwu.logic.usecase

import com.github.miwu.logic.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.logic.usecase.home.ConvertHomeDataUseCase
import com.github.miwu.logic.usecase.login.LoginUseCase
import com.github.miwu.logic.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.logic.usecase.scene.GetHomeScenesUseCase
import com.github.miwu.logic.usecase.state.MapFragmentStateUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetSortedDevicesUseCase(get(), get()) }
    factory { MapFragmentStateUseCase() }
    factory { GetSortedRoomsUseCase(get()) }
    factory { GetHomeScenesUseCase(get()) }
    factory { ConvertHomeDataUseCase(get(), get(org.koin.core.qualifier.named("io"))) }
    factory { LoginUseCase(get(), get()) }
}
