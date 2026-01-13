/*
* Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/


package miwu.miot.kmp.plugin

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.clone
import io.ktor.http.decodeCookieValue
import io.ktor.http.fromCookieToGmtDate
import io.ktor.http.parseClientCookiesHeader
import io.ktor.http.renderCookieHeader
import io.ktor.util.AttributeKey
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.toLowerCasePreservingASCIIRules
import io.ktor.utils.io.KtorDsl
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private val LOGGER = KtorSimpleLogger("io.ktor.client.plugins.HttpCookies")

/**
 * Support parsing non-compilant `Expires` dates of Set-Cookie header
 * [KTOR-9235](https://youtrack.jetbrains.com/issue/KTOR-9235/)
 */
class SupportInvalidExpiresHttpCookies internal constructor(
    private val storage: CookiesStorage,
    private val defaults: List<suspend CookiesStorage.() -> Unit>
) : Closeable {
    @OptIn(DelicateCoroutinesApi::class)
    private val initializer: Job = GlobalScope.launch(Dispatchers.Unconfined) {
        defaults.forEach { it(storage) }
    }

    public suspend fun get(requestUrl: Url): List<Cookie> {
        initializer.join()
        return storage.get(requestUrl)
    }

    internal suspend fun captureHeaderCookies(builder: HttpRequestBuilder) {
        with(builder) {
            val url = builder.url.clone().build()
            val cookies = headers[HttpHeaders.Cookie]?.let { cookieHeader ->
                LOGGER.trace("Saving cookie $cookieHeader for ${builder.url}")
                parseClientCookiesHeader(cookieHeader).map { (name, encodedValue) ->
                    Cookie(name, encodedValue, encoding = CookieEncoding.RAW)
                }
            }
            cookies?.forEach { storage.addCookie(url, it) }
        }
    }

    internal suspend fun sendCookiesWith(builder: HttpRequestBuilder) {
        val cookies = get(builder.url.clone().build())

        with(builder) {
            if (cookies.isNotEmpty()) {
                val cookieHeader = renderClientCookies(cookies)
                headers[HttpHeaders.Cookie] = cookieHeader
                LOGGER.trace("Sending cookie $cookieHeader for ${builder.url}")
            } else {
                headers.remove(HttpHeaders.Cookie)
            }
        }
    }

    internal suspend fun saveCookiesFrom(response: HttpResponse) {
        val url = response.request.url
        response.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            LOGGER.trace("Received cookie $it in response for ${response.call.request.url}")
        }
        response.headers.getAll(HttpHeaders.SetCookie)
            ?.flatMap { it.splitSetCookieHeader() }
            ?.map { parseServerSetCookieHeader(it) }
            ?.forEach { storage.addCookie(url, it) }
    }

    override fun close() {
        storage.close()
    }

    @KtorDsl
    public class Config {
        private val defaults = mutableListOf<suspend CookiesStorage.() -> Unit>()

        public var storage: CookiesStorage = AcceptAllCookiesStorage()

        public fun default(block: suspend CookiesStorage.() -> Unit) {
            defaults.add(block)
        }

        internal fun build(): SupportInvalidExpiresHttpCookies = SupportInvalidExpiresHttpCookies(storage, defaults)
    }

    public companion object : HttpClientPlugin<Config, SupportInvalidExpiresHttpCookies> {
        override fun prepare(block: Config.() -> Unit): SupportInvalidExpiresHttpCookies =
            Config().apply(block).build()

        override val key: AttributeKey<SupportInvalidExpiresHttpCookies> = AttributeKey("HttpCookies")

        override fun install(plugin: SupportInvalidExpiresHttpCookies, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.captureHeaderCookies(context)
            }
            scope.sendPipeline.intercept(HttpSendPipeline.State) {
                plugin.sendCookiesWith(context)
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.State) { response ->
                plugin.saveCookiesFrom(response)
            }
        }
    }
}

private fun renderClientCookies(cookies: List<Cookie>): String =
    cookies.joinToString("; ", transform = ::renderCookieHeader)

private val loweredPartNames =
    setOf("max-age", "expires", "domain", "path", "secure", "httponly", "\$x-enc")

private fun String.toIntClamping(): Int = toLong().coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

fun parseServerSetCookieHeader(cookiesHeader: String): Cookie {
    val asMap = parseClientCookiesHeader(cookiesHeader, false)
    val first = asMap.entries.first { !it.key.startsWith("$") }
    val encoding = asMap["\$x-enc"]?.let { CookieEncoding.valueOf(it) } ?: CookieEncoding.RAW
    val loweredMap = asMap.mapKeys { it.key.toLowerCasePreservingASCIIRules() }

    return Cookie(
        name = first.key,
        value = decodeCookieValue(first.value, encoding),
        encoding = encoding,
        maxAge = loweredMap["max-age"]?.toIntClamping(),
        expires = runCatching { loweredMap["expires"]?.fromCookieToGmtDate() }.getOrNull(),
        domain = loweredMap["domain"],
        path = loweredMap["path"],
        secure = "secure" in loweredMap,
        httpOnly = "httponly" in loweredMap,
        extensions = asMap.filterKeys {
            it.toLowerCasePreservingASCIIRules() !in loweredPartNames && it != first.key
        }
    )
}

fun String.splitSetCookieHeader(): List<String> {
    var comma = indexOf(',')

    if (comma == -1) {
        return listOf(this)
    }

    val result = mutableListOf<String>()
    var current = 0

    var equals = indexOf('=', comma)
    var semicolon = indexOf(';', comma)
    while (current < length && comma > 0) {
        if (equals < comma) {
            equals = indexOf('=', comma)
        }

        var nextComma = indexOf(',', comma + 1)
        while (nextComma in 0..<equals) {
            comma = nextComma
            nextComma = indexOf(',', nextComma + 1)
        }

        if (semicolon < comma) {
            semicolon = indexOf(';', comma)
        }

        // No more keys remaining.
        if (equals < 0) {
            result += substring(current)
            return result
        }

        // No ';' between ',' and '=' => We're on a header border.
        if (semicolon == -1 || semicolon > equals) {
            result += substring(current, comma)
            current = comma + 1
            // Update comma index at the end of loop.
        }

        // ',' in value, skip it and find next.
        comma = nextComma
    }

    if (current < length) {
        result += substring(current)
    }

    return result
}
