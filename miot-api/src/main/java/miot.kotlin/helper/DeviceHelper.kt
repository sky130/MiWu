package miot.kotlin.helper

import android.bluetooth.BluetoothClass.Device
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.Miot
import miot.kotlin.MiotManager
import miot.kotlin.MiotManager.gson
import miot.kotlin.model.miot.MiotDevices
import org.json.JSONObject

data class GetAtt(val siid: Int, val piid: Int)
data class SetAtt(val siid: Int, val piid: Int, val value: Any)
data class Action(val siid: Int, val aiid: Int, val `in`: ArrayList<Any> = arrayListOf())


suspend fun MiotDevices.Result.Device.getAtt(miot: Miot, vararg att: GetAtt) =
    miot.getDeviceAtt(this, att)

suspend fun MiotDevices.Result.Device.setAtt(miot: Miot, vararg att: SetAtt) =
    miot.setDeviceAtt(this, att)

suspend fun MiotDevices.Result.Device.getIconUrl(): String? = withContext(Dispatchers.IO) {
    try {
        val url =
            "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${this@getIconUrl.model}"
        val json = MiotManager.get(url)
        val info = gson.fromJson(json, DeviceInfo::class.java)
        if (info.code != 0) return@withContext null
        return@withContext info.data.realIcon
    } catch (_: Exception) {
        return@withContext null
    }
}

data class DeviceInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data
) {
    data class Data(@SerializedName("realIcon") val realIcon: String)
}

