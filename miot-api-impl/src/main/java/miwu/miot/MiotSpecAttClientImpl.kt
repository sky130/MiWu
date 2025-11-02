package miwu.miot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.exception.MiotDeviceException
import miwu.miot.exception.MiotParseException
import miwu.miot.ktx.OkHttpClient
import miwu.miot.ktx.Retrofit
import miwu.miot.ktx.create
import miwu.miot.model.att.SpecAtt
import miwu.miot.service.SpecService
import miwu.miot.utils.gson
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory

class MiotSpecAttClientImpl : MiotSpecAttClient {
    private val specRetrofit = Retrofit(
        url = SPEC_SERVER_URL, factories = arrayOf(
            GsonConverterFactory.create(),
        )
    )
    private val spec = specRetrofit.create<SpecService>()
    private val client = OkHttpClient { }

    override suspend fun getSpecAtt(urn: String): Result<SpecAtt> = withContext(Dispatchers.IO) {
        runCatching {
            spec.getInstance(urn)
        }.recoverCatching {
            throw MiotDeviceException.specNotFound(urn)
        }
    }

    override suspend fun getSpecMultiLanguage(urn: String): Result<String> =
        withContext(Dispatchers.IO) {
            val response = spec.getSpecMultiLanguage(urn).string()
            when (response) {
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
        gson.fromJson(
            JSONObject(language).getJSONObject("data").getJSONObject(languageCode).toString(),
            HashMap::class.java
        ) as Map<String, String>
    }.recoverCatching {
        throw MiotParseException.jsonParse(it)
    }
}