package miwu.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual fun platformIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
