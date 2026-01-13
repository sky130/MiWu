package miwu.miot.kmp.ktx

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.IO: CoroutineDispatcher
    get() = Dispatchers.Default