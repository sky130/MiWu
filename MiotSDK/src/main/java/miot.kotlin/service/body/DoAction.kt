package miot.kotlin.service.body

import com.google.gson.annotations.SerializedName

data class ActionBody(
    @SerializedName("params") val params: Action,
) {
    data class Action(
        @SerializedName("did") val did: String,
        @SerializedName("siid") val siid: Int,
        @SerializedName("aiid") val aiid: Int,
    )
}