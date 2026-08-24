package com.github.miwu.utils

import kotlinx.coroutines.CancellationException

suspend inline fun <T, R> T.runCatchingSuspend(crossinline block: suspend T.() -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }

suspend inline fun <R> runCatchingSuspend(crossinline block: suspend () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }

fun Throwable.throwIfCancelled() {
    if (this is CancellationException) throw this
}

fun <T> Result<T>.throwIfCancelled() =
    onFailure { if (it is CancellationException) throw it }