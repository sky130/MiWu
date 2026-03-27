package com.github.miwu

import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.ui.viewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miwu.miot.Client
import miwu.miot.Provider
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.Provider
import org.koin.dsl.module

val appModule = module {
    includes(
        repositoryModule,
        viewModelModule,
        databaseModule,
        dataStoreModule,
    )
    includes(
        MiotApiKoinModule.JVM.Client,
        MiotApiKoinModule.JVM.Provider,
    )
    single<Job> { Job() }
    single { CoroutineScope(get<Job>()) }
}