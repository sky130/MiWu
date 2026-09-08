package com.github.miwu.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Qualifier
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create

val dispatchersModule = module {
    single { create(::dispatcherIO) }
    single { create(::dispatcherDefault) }
    single { create(::dispatcherMain) }
    single { create(::coroutineScope) }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class UiDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class AppScope

@IoDispatcher
fun dispatcherIO(): CoroutineDispatcher = Dispatchers.IO

@UiDispatcher
fun dispatcherMain(): CoroutineDispatcher = Dispatchers.Main

@DefaultDispatcher
fun dispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

@AppScope
fun coroutineScope(
    @DefaultDispatcher default: CoroutineDispatcher
) = CoroutineScope(SupervisorJob() + default)