package com.github.miwu.logic.repository

import com.github.miwu.logic.repository.impl.CacheRepositoryImpl
import com.github.miwu.logic.repository.impl.LocalRepositoryImpl
import com.github.miwu.logic.repository.impl.MiotRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MiotRepository> {
        MiotRepositoryImpl()
    }
    single<LocalRepository> {
        LocalRepositoryImpl()
    }
    single<CacheRepository> {
        CacheRepositoryImpl()
    }
}