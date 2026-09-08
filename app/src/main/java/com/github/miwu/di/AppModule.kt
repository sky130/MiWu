package com.github.miwu.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import miwu.miot.Provider
import miwu.miot.common.MiotApiKoinModule
import org.koin.dsl.module
import org.koin.core.qualifier.named

val appIoDispatcher = named("app_io_dispatcher")
val appMainDispatcher = named("app_main_dispatcher")
val appDefaultDispatcher = named("app_default_dispatcher")
val appScope = named("app_scope")

val appModule = module {
    includes(
        MiotApiKoinModule.JVM.Provider,
    )
    includes(
        domainModule,
        viewModelModule,
        dataModule,
        platformModule,
    )
    single(appIoDispatcher) { Dispatchers.IO }
    single(appDefaultDispatcher) { Dispatchers.Default }
    single(appMainDispatcher) { Dispatchers.Main.immediate }
    single(appScope) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(appDefaultDispatcher))
    }
}
