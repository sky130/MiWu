package com.github.miwu.logic.repository

import com.github.miwu.logic.auth.AuthService
import com.github.miwu.logic.repository.impl.CacheRepositoryImpl
import com.github.miwu.logic.repository.impl.LocalRepositoryImpl
import com.github.miwu.logic.repository.impl.MiotRepositoryImpl
import com.github.miwu.logic.service.DeviceTileRefreshService
import com.github.miwu.logic.device.DeviceSessionResolver
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthService(get(), get(), get()) }
    single<MiotRepository> {
        MiotRepositoryImpl(get(), get(), get(), get())
    }
    single<LocalRepository> {
        LocalRepositoryImpl(get(), get(), get(), get())
    }
    single<CacheRepository> {
        CacheRepositoryImpl(get(), get(org.koin.core.qualifier.named("io")))
    }
    single { DeviceTileRefreshService(get(), get()) }
    factory { DeviceSessionResolver(get(), get(), get()) }
}
