package miwu.miot.kmp.di

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Qualifier
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create

val dispatchersModule = module {
    single { create(::dispatcherIO) }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class IoDispatcher

@IoDispatcher
fun dispatcherIO(): CoroutineDispatcher = platformIoDispatcher()

internal expect fun platformIoDispatcher(): CoroutineDispatcher
