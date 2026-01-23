package com.github.miwu.logic.repository

import com.github.miwu.logic.repository.impl.AppRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AppRepository> {
        AppRepositoryImpl()
    }
    single {
        LocalRepository()
    }
    single {
        DeviceRepository()
    }
}