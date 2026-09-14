package miwu.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun platformIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
