package miwu.miot.kmp.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import miwu.miot.model.JsonAnySerializer

@Serializable
data class ActionBody(
    @SerialName("params") val params: Action,
) {
    @Serializable
    data class Action(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("aiid") val aiid: Int,
        @SerialName("in") val `in`: ArrayList<@Serializable(with = JsonAnySerializer::class) Any> = arrayListOf()
    )
}