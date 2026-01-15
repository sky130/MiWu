package com.github.miwu.logic.repository

import org.koin.dsl.module

val repositoryModule = module {
    single {
        AppRepository()
    }
    single {
        LocalRepository()
    }
    single {
        DeviceRepository()
    }
}