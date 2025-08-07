package com.github.miwu.logic.repository

import org.koin.dsl.module

val RepositoryModule = module {
    single {
        AppRepository()
    }
    single {
        DeviceRepository()
    }
}