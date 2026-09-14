package com.github.miwu.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import miwu.dispatchers.DefaultDispatcher
import miwu.dispatchers.DispatcherModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module(includes = [DispatcherModule::class])
@ComponentScan("com.github.miwu")
class AppModule {
    @Singleton
    @AppScope
    fun coroutineScope(
        @DefaultDispatcher default: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + default)
}
