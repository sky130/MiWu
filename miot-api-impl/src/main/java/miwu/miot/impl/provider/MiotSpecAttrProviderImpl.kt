package miwu.miot.impl.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.create
import miwu.miot.utils.json
import miwu.miot.service.SpecService
import miwu.miot.common.SPEC_SERVER_URL
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.exception.MiotParseException
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevices.Result.Info
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.miot.utils.get

class MiotSpecAttrProviderImpl : MiotSpecAttrProvider {
    private val client = OkHttpClient()
    private val specRetrofit = Retrofit(
        baseUrl = SPEC_SERVER_URL,
        client = client,
        factories = arrayOf(
            JsonConverterFactory(),
        )
    )
    private val specService = specRetrofit.create<SpecService>()

    override suspend fun getSpecAtt(urn: String) = withContext(Dispatchers.IO) {
        runCatching {
            specService.getInstance(urn)
        }.recoverCatching {
            throw MiotDeviceException.specNotFound(urn, it)
        }
    }

    override suspend fun getSpecMultiLanguage(urn: String) = withContext(Dispatchers.IO) {
        specService.getSpecMultiLanguage(urn).use { response ->
            runCatching {
                when (val string = response.string()) {
                    "model not found" -> throw MiotDeviceException.modelNotFound(urn)
                    "urn is not iot namespace" -> throw MiotDeviceException.urnFormatError(urn)
                    else -> string
                }
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
            val info = client.get<Info>(url).getOrThrow()
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