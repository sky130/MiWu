package miwu.miot.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.ktx.OkHttpClient
import miwu.miot.ktx.get
import miwu.miot.model.miot.MiotDevices.Result.Device.Info

object MiotIconHelper {
    private val iconClient by lazy {
        OkHttpClient {  }
    }

    suspend fun getIconUrl(model: String) = withContext(Dispatchers.IO) {
        try {
            val url = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${model}"
            val info = iconClient.get<Info>(url)
            if (info.code != 0) return@withContext null
            return@withContext info.data.realIcon
        } catch (e: Exception) {
            e
            return@withContext null
        }
    }
}