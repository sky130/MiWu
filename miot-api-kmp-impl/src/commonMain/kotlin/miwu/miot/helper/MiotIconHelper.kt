package miwu.miot.helper

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.ktx.IO
import miwu.miot.model.miot.MiotDevices.Result.Info
import miwu.miot.ktx.json

object MiotIconHelper {
    private val httpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun getIconUrl(model: String) = withContext(Dispatchers.IO) {
        try {
            val url = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${model}"
            val info = httpClient.get(url).body<Info>()
            if (info.code != 0) return@withContext null
            return@withContext info.data.realIcon
        } catch (e: Exception) {
            e
            return@withContext null
        }
    }
}