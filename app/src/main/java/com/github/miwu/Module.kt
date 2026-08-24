package com.github.miwu

import com.github.miwu.logic.database.databaseModule
import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.repository.repositoryModule
import com.github.miwu.logic.usecase.useCaseModule
import com.github.miwu.ui.viewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import miwu.miot.Client
import miwu.miot.Provider
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.Provider
import org.koin.dsl.module
import org.koin.core.qualifier.named

val appModule = module {
    includes(
        MiotApiKoinModule.JVM.Client,
        MiotApiKoinModule.JVM.Provider,
    )
    includes(
        repositoryModule,
        useCaseModule,
        viewModelModule,
        databaseModule,
        dataStoreModule,
    )
    single(named("io")) { Dispatchers.IO }
    single(named("default")) { Dispatchers.Default }
    single(named("ui")) { Dispatchers.Main.immediate }
    single { CoroutineScope(SupervisorJob() + get<kotlinx.coroutines.CoroutineDispatcher>(named("default"))) }
}
