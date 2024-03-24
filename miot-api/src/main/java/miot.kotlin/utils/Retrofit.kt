package miot.kotlin.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

inline fun <reified T> Retrofit.create(): T = this.create(T::class.java)


fun <T> Call<T>.onSuccess(block: suspend CoroutineScope.(value: T) -> Unit) =
    MutableRequest(this).apply {
        onSuccessBlock = block
    } as Request<T>

fun <T> Request<T>.onSuccess(block: suspend CoroutineScope.(value: T) -> Unit) =
    (this as MutableRequest).apply {
        onSuccessBlock = block
    }

fun <T> Call<T>.onEach(block: suspend CoroutineScope.() -> Unit) =
    MutableRequest(this).apply {
        onEachBlock = block
    } as Request<T>

fun <T> Request<T>.onEach(block: suspend CoroutineScope.() -> Unit) =
    (this as MutableRequest).apply {
        onEachBlock = block
    }

fun <T> Call<T>.onFailure(block: suspend CoroutineScope.(error: Throwable) -> Unit) =
    MutableRequest(this).apply {
        onFailureBlock = block
    } as Request<T>

fun <T> Request<T>.onFailure(block: suspend CoroutineScope.(error: Throwable) -> Unit) =
    (this as MutableRequest).apply {
        onFailureBlock = block
    }

fun <T> Call<T>.onNull(block: suspend CoroutineScope.(response: Response<T>) -> Unit) =
    MutableRequest(this).apply {
        onNullBlock = block
    } as Request<T>

fun <T> Request<T>.onNull(block: suspend CoroutineScope.(response: Response<T>) -> Unit) =
    (this as MutableRequest).apply {
        onNullBlock = block
    }

suspend fun <T> Call<T>.call() = MutableRequest(this).call()

open class MutableRequest<T>(private val call: Call<T>) : Request<T> {
    var onSuccessBlock: (suspend CoroutineScope.(value: T) -> Unit)? = null
    var onFailureBlock: (suspend CoroutineScope.(error: Throwable) -> Unit)? = null
    var onNullBlock: (suspend CoroutineScope.(response: Response<T>) -> Unit)? = null
    var onEachBlock: (suspend CoroutineScope.() -> Unit)? = null

    override fun call(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                call.execute().let { body ->
                    body.body().let {
                        withContext(Dispatchers.Main) {
                            if (it == null) {
                                onNullBlock?.invoke(this, body)
                            } else {
                                onSuccessBlock?.invoke(this, it)
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    onFailureBlock?.invoke(this, e)
                }
            }
            onEachBlock?.invoke(this)
        }
    }

    override suspend fun call() = withContext(Dispatchers.IO) {
        try {
            call.execute().let {
                Result.Success<T>(it.body()!!, it)
            }
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

}

interface Request<T> {

    fun call(scope: CoroutineScope)

    suspend fun call(): Result<T>

}

sealed class Result<T> {
    data class Success<T>(val value: T, val response: Response<T>) : Result<T>()

    data class Failure<T>(val error: Throwable) : Result<T>()
}

suspend fun <T> Result<T>.onSuccess(block: suspend (value: T) -> Unit) {
    if (this is Result.Success) {
        block(value)
    }
}

suspend fun <T> Result<T>.onFailure(block: suspend (error: Throwable) -> Unit) {
    if (this is Result.Failure) {
        block(error)
    }
}

inline fun <T> T?.isNull(block: () -> Unit): T? {
    if (this == null) block()
    return this
}

inline fun <T> T?.isNotNull(block: T.() -> Unit): T? {
    if (this != null) block()
    return this
}