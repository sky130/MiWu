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
import miwu.miot.kmp.utils.json
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.common.getNonce
import miwu.miot.model.MiotUser
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import okio.use
import kotlin.io.encoding.Base64


@OptIn(InternalSerializationApi::class)
class MiotAuth internal constructor(internal val user: MiotUser?) {

    @Suppress("UNCHECKED_CAST")
    fun transformRequestBody(request: HttpRequestBuilder, content: Any, bodyType: TypeInfo?): Any {
        val originBody = request.body

        user ?: throw IllegalArgumentException("user not found.")

        val ssecurity = user.ssecurity
        val serviceToken = user.serviceToken

        if (serviceToken.isEmpty() || ssecurity.isEmpty())
            throw IllegalArgumentException("serviceToken or securityToken not found.")

        val data = json.encodeToString(
            originBody::class.serializer() as KSerializer<Any>,
            originBody
        )

        val nonce = getNonce()
        val signedNonce = generateSignedNonce(ssecurity, nonce)
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
        private var user: MiotUser? = null

        fun user(miotUser: MiotUser) {
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

    fun generateSignedNonce(secret: String, nonce: String): String {
        val secretBytes = Base64.decode(secret)
        val nonceBytes = Base64.decode(nonce)
        val hash = Buffer().use { buffer ->
            buffer.write(secretBytes)
            buffer.write(nonceBytes)
            buffer.sha256()
        }
        return Base64.encode(hash.toByteArray())
    }

    fun generateSignature(
        uri: String,
        signedNonce: String,
        nonce: String,
        data: String
    ): String {
        val data = "$uri&$signedNonce&$nonce&data=$data".encodeToByteArray()
        val key = ByteString.of(*Base64.decode(signedNonce))
        val digest = data.toByteString().hmacSha256(key)
        return Base64.encode(digest.toByteArray())
    }
}
