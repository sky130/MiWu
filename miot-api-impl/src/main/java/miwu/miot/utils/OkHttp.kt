package miwu.miot.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset


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

internal suspend inline fun <reified T> OkHttpClient.get(
    url: String,
    body: RequestBody? = null,
): Result<T> = withContext(Dispatchers.IO) {
    runCatching {
        val request = Request.Builder()
            .url(url)
            .apply { if (body != null) post(body) }
            .build()
        newCall(request).execute().use { response ->
            return@runCatching when (T::class) {
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
