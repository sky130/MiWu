package miwu.miot.model.att

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.model.JsonAnySerializer

typealias ActionList = ArrayList<Action>

@Serializable
data class Action(
    @SerialName("did") val did: String,
    @SerialName("iid") val iid: String,
    @SerialName("siid") val siid: Int,
    @SerialName("aiid") val aiid: Int,
    @SerialName("out") val out: ArrayList<@Serializable(with = JsonAnySerializer::class) Any>,
    @SerialName("code") val code: Int,
    @SerialName("exe_time") val exeTime: Int,
)
