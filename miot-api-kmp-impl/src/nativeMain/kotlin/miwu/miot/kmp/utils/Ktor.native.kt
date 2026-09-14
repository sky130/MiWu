package miwu.miot.kmp.utils

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

actual fun MiotHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(block)