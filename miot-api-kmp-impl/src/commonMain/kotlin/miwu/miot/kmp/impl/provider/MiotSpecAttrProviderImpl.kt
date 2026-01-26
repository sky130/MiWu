package miwu.miot.kmp.impl.provider

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.kmp.utils.IO
import miwu.miot.kmp.utils.json
import miwu.miot.common.SPEC_SERVER_URL
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.exception.MiotParseException
import miwu.miot.kmp.service.createSpecService
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevices.Result.Info
import miwu.miot.provider.MiotSpecAttrProvider

class MiotSpecAttrProviderImpl : MiotSpecAttrProvider {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }
    private val ktorfit = Ktorfit.Builder()
        .httpClient(httpClient)
        .baseUrl(SPEC_SERVER_URL)
        .build()
    private val specService = ktorfit.createSpecService()

    override suspend fun getSpecAtt(urn: String) = withContext(Dispatchers.IO) {
        runCatching {
            specService.getInstance(urn)
        }.recoverCatching {
            throw MiotDeviceException.specNotFound(urn, it)
        }
    }

    override suspend fun getSpecMultiLanguage(urn: String) = withContext(Dispatchers.IO) {
        runCatching {
            when (val response = specService.getSpecMultiLanguage(urn)) {
                "model not found" -> throw MiotDeviceException.modelNotFound(urn)
                "urn is not iot namespace" -> throw MiotDeviceException.urnFormatError(urn)
                else -> response
            }
        }
    }

    override suspend fun getSpecAttWithLanguage(
        urn: String,
        languageCode: String
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val att: SpecAtt = getSpecAtt(urn).getOrThrow()
            val language = getSpecMultiLanguage(urn).getOrThrow()
            val map = getSpecAttLanguageMap(
                language,
                languageCode
            ).getOrElse { return@withContext Result.success(att) }
            att.initVariable()
            att.convertLanguage(map)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSpecAttLanguageMap(
        language: String,
        languageCode: String
    ) = runCatching {
        json.decodeFromString<LanguageMap>(language).data[languageCode] ?: emptyMap()
    }.recoverCatching {
        throw MiotParseException.jsonParse(it)
    }

    override suspend fun getIconUrl(model: String) = withContext(Dispatchers.IO) {
        runCatching {
            val url = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${model}"
            val info = httpClient.get(url).body<Info>()
            if (info.code != 0) throw MiotClientException.getIconUrlFailed(model)
            info.data.realIcon
        }
    }

    @Serializable
    private data class LanguageMap(
        @SerialName("data")
        val data: Map<String, Map<String, String>>
    )
}