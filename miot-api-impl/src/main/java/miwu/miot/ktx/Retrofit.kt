package miwu.miot.ktx

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


fun Retrofit(
    url: String,
    factories: Array<Converter.Factory>,
    client: OkHttpClient? = null,
    block: (Retrofit.Builder.() -> Unit)? = null
) =
    Retrofit.Builder().baseUrl(url)
        .apply {
            factories.forEach { addConverterFactory(it) }
            client?.let { client(it) }
            block?.invoke(this)
        }
        .build()

inline fun <reified T> Retrofit.create(): T = this.create(T::class.java)

val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun SerializationJsonFactory() =
    json.asConverterFactory("application/json; charset=utf-8".toMediaType())