package miwu.miot.model.att

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.model.JsonAnySerializer

@Serializable
data class PropertyResponse(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: ArrayList<PropertyResult>? = null,
)

@Serializable
data class PropertyResult(
    @SerialName("did") val did: String,
    @SerialName("iid") val iid: String,
    @SerialName("siid") val siid: Int,
    @SerialName("piid") val piid: Int,
    @SerialName("value") val value: @Serializable(with = JsonAnySerializer::class) Any? = null,
    @SerialName("code") val code: Int,
    @SerialName("updateTime") val updateTime: Long? = null,
    @SerialName("exe_time") val exeTime: Int,
)
