package miwu.miot.kmp.plugin

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.util.AttributeKey
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.KtorDsl
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import miwu.miot.kmp.MIOT_SERVER_URL
import miwu.miot.kmp.generateSignature
import miwu.miot.kmp.generateSignedNonce
import miwu.miot.ktx.json
import miwu.miot.model.MiotUser
import miwu.miot.utils.getNonce


@OptIn(InternalSerializationApi::class)
class MiotAuth internal constructor(internal val user: (() -> MiotUser)?) {

    @Suppress("UNCHECKED_CAST")
    fun transformRequestBody(request: HttpRequestBuilder, content: Any, bodyType: TypeInfo?): Any {
        val originBody = request.body

        val (userId, securityToken, serviceToken, deviceId) = user?.invoke()
            ?: throw IllegalArgumentException("user not found.")

        if (serviceToken.isEmpty() || securityToken.isEmpty())
            throw IllegalArgumentException("serviceToken or securityToken not found.")

        val data = json.encodeToString(
            originBody::class.serializer() as KSerializer<Any>,
            originBody
        )

        val nonce = getNonce()
        val signedNonce = generateSignedNonce(securityToken, nonce)
        val signature = generateSignature(
            request.url.toString().replace(MIOT_SERVER_URL, "/"),
            signedNonce,
            nonce,
            data
        )

        request.contentType(ContentType.Application.FormUrlEncoded)

        return FormDataContent(parameters {
            append("_nonce", nonce)
            append("data", data)
            append("signature", signature)
        })
    }

    @KtorDsl
    class Config {
        private var user: (() -> MiotUser)? = null

        fun user(miotUser: () -> MiotUser) {
            this.user = miotUser
        }

        internal fun build(): MiotAuth = MiotAuth(user)
    }

    companion object : HttpClientPlugin<Config, MiotAuth> {
        override fun prepare(block: Config.() -> Unit): MiotAuth = Config().apply(block).build()

        override val key: AttributeKey<MiotAuth> = AttributeKey("MiotAuth")

        override fun install(
            plugin: MiotAuth,
            scope: HttpClient
        ) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) {
                proceedWith(
                    plugin.transformRequestBody(
                        context,
                        subject,
                        context.bodyType
                    )
                )
            }
        }
    }
}
