package miwu.miot.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import miwu.miot.exception.MiotHttpException
import kotlinx.coroutines.CancellationException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


fun OkHttpClient.Builder.userAgent(ua: String): OkHttpClient.Builder = addInterceptor { chain ->
    chain.proceed(
        chain.request()
            .newBuilder()
            .removeHeader("User-Agent")
            .addHeader("User-Agent", ua)
            .build()
    )
}

fun OkHttpClient(block: OkHttpClient.Builder.() -> Unit = {}): OkHttpClient =
    OkHttpClient.Builder().apply(block).build()

fun OkHttpClient.Builder.miotTimeouts(): OkHttpClient.Builder =
    connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)

fun OkHttpClient.close() {
    dispatcher.executorService.shutdown()
    connectionPool.evictAll()
    cache?.close()
}

internal suspend inline fun <reified T> OkHttpClient.get(
    url: String,
    body: RequestBody? = null,
    headers: Map<String, String> = emptyMap(),
    dispatcher: CoroutineDispatcher,
): Result<T> = withContext(dispatcher) {
    runCatchingSuspend {
        val request = Request.Builder()
            .url(url)
            .apply { if (body != null) post(body) }
            .apply { headers.forEach(::addHeader) }
            .build()
        newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw MiotHttpException("HTTP ${response.code} for ${request.url}")
            }
            return@runCatchingSuspend when (T::class) {
                Response::class -> response as T
                String::class -> response.body.string() as T
                else -> response.body.string().to<T>().getOrThrow()
            }
        }
    }
}

fun FormBody(block: FormBody.Builder.() -> Unit): FormBody = FormBody.Builder().apply(block).build()

fun RequestBody.readToString(): String {
    Buffer().apply {
        writeTo(this)
        val contentType = contentType()
        if (contentType != null) {
            return readString(
                contentType.charset(
                    Charset.forName("UTF-8")
                )!!
            )
        }
    }
    throw RuntimeException("data of requestBody is empty")
}

fun Request.Builder.addHeaders(vararg headers: Pair<String, String>): Request.Builder {
    for ((k, v) in headers) {
        addHeader(k, v)
    }
    return this
}
