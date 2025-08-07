package miwu.miot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        url = SPEC_SERVER_URL,
        factories = arrayOf(
            GsonConverterFactory.create(),
        )
    )
    private val spec = specRetrofit.create<SpecService>()
    private val client = OkHttpClient { }

    override suspend fun getSpecAtt(urn: String) : SpecAtt? = withContext(Dispatchers.IO) {
        spec.getInstance(urn)
    }

    override suspend fun getSpecMultiLanguage(urn: String) = withContext(Dispatchers.IO) {
        spec.getSpecMultiLanguage(urn).string()
    }

    override suspend fun getSpecAttWithLanguage(urn: String, languageCode: String): SpecAtt? =
        withContext(Dispatchers.IO) {
            runCatching {
                val att = getSpecAtt(urn)
                att ?: return@withContext null
                val language = getSpecMultiLanguage(urn)
                val map = getSpecAttLanguageMap(language, languageCode) ?: return@runCatching att
                att.initVariable()
                att.convertLanguage(map)
            }.onSuccess {
                return@withContext it
            }.onFailure {
                it.printStackTrace()
            }
            null
        }

    @Suppress("UNCHECKED_CAST")
    override fun getSpecAttLanguageMap(language: String, languageCode: String): Map<String, String>? {
        return try {
            gson.fromJson(
                JSONObject(language)
                    .getJSONObject("data")
                    .getJSONObject(languageCode).toString(),
                HashMap::class.java
            ) as Map<String, String>
        } catch (_: Exception) {
            null
        }
    }
}