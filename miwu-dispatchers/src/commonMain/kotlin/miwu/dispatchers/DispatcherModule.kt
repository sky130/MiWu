package miwu.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

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

@Module
class DispatcherModule {
    @Singleton
    @IoDispatcher
    fun dispatcherIO(): CoroutineDispatcher = platformIoDispatcher()

    @Singleton
    @UiDispatcher
    fun dispatcherMain(): CoroutineDispatcher = Dispatchers.Main

    @Singleton
    @DefaultDispatcher
    fun dispatcherDefault(): CoroutineDispatcher = Dispatchers.Default
}

internal expect fun platformIoDispatcher(): CoroutineDispatcher
