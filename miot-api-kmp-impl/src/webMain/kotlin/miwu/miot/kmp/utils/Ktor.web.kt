package miwu.miot.kmp.utils

import io.ktor.client.HttpClient

actual fun MiotHttpClient(block: io.ktor.client.HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(block)