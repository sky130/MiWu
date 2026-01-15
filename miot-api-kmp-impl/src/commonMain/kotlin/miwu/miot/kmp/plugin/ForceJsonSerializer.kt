package miwu.miot.kmp.plugin

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.statement.bodyAsText
import io.ktor.http.content.NullBody
import io.ktor.util.AttributeKey
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.serializer
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.KtorDsl
import kotlinx.serialization.json.Json


@OptIn(InternalAPI::class)
class ForceJsonSerializer internal constructor(internal val json: Json) {

    suspend fun transformResponseBody(
        response: HttpResponse,
        content: ByteReadChannel,
        typeInfo: TypeInfo
    ): Any? = runCatching {
        json.decodeFromString(typeInfo.serializer(), response.bodyAsText())
    }.getOrNull()

    @KtorDsl
    class Config {
        private var json = miwu.miot.kmp.utils.json

        fun json(json: Json) {
            this.json = json
        }

        internal fun build(): ForceJsonSerializer = ForceJsonSerializer(json)
    }

    companion object : HttpClientPlugin<Config, ForceJsonSerializer> {
        override fun prepare(block: Config.() -> Unit): ForceJsonSerializer =
            Config().apply(block).build()

        override val key: AttributeKey<ForceJsonSerializer> = AttributeKey("ForceJsonSerializer")

        override fun install(
            plugin: ForceJsonSerializer,
            scope: HttpClient
        ) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) {
                val (typeInfo, content) = subject
                if (content !is ByteReadChannel) return@intercept
                val newContent = plugin.transformResponseBody(context.response, content, typeInfo)
                    ?: return@intercept
                if (newContent !is NullBody && !typeInfo.type.isInstance(newContent)) {
                    throw IllegalStateException(
                        "transformResponseBody returned $newContent but expected value of type $typeInfo"
                    )
                }
                proceedWith(HttpResponseContainer(typeInfo, newContent))
            }
        }
    }
}