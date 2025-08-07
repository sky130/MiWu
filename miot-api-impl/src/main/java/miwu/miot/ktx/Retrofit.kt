package miwu.miot.ktx

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit


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
