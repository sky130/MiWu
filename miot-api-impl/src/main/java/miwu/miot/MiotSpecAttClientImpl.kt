package miwu.miot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.exception.MiotDeviceException
import miwu.miot.exception.MiotParseException
import miwu.miot.ktx.OkHttpClient
import miwu.miot.ktx.Retrofit
import miwu.miot.ktx.SerializationJsonFactory
import miwu.miot.ktx.create
import miwu.miot.ktx.json
import miwu.miot.model.att.SpecAtt
import miwu.miot.service.SpecService

class MiotSpecAttClientImpl : MiotSpecAttClient {
    private val specRetrofit = Retrofit(
        url = SPEC_SERVER_URL, factories = arrayOf(
            SerializationJsonFactory(),
        )
    )
    private val spec = specRetrofit.create<SpecService>()
    private val client = OkHttpClient { }

    override suspend fun getSpecAtt(urn: String): Result<SpecAtt> = withContext(Dispatchers.IO) {
        runCatching {
            spec.getInstance(urn)
        }.recoverCatching {
            throw MiotDeviceException.specNotFound(urn, it)
        }
    }

    override suspend fun getSpecMultiLanguage(urn: String): Result<String> =
        withContext(Dispatchers.IO) {
            when (val response = spec.getSpecMultiLanguage(urn).string()) {
                "model not found" -> Result.failure(MiotDeviceException.modelNotFound(urn))
                "urn is not iot namespace" -> Result.failure(MiotDeviceException.urnFormatError(urn))
                else -> Result.success(response)
            }
        }

    override suspend fun getSpecAttWithLanguage(
        urn: String, languageCode: String
    ): Result<SpecAtt> = withContext(Dispatchers.IO) {
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
        language: String, languageCode: String
    ): Result<Map<String, String>> = runCatching {
        json.decodeFromString<LanguageMap>(language).data[languageCode] ?: emptyMap()
    }.recoverCatching {
        throw MiotParseException.jsonParse(it)
    }

    @Serializable
    private data class LanguageMap(
        @SerialName("data")
        val data: Map<String, Map<String, String>>
    )
}