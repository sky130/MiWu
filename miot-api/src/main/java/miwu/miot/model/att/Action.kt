package miwu.miot.model.att

import com.google.gson.annotations.SerializedName

data class Action(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Att?,
) {
    data class Att(
        @SerializedName("did") val did: String,
        @SerializedName("iid") val iid: String,
        @SerializedName("siid") val siid: Int,
        @SerializedName("aiid") val aiid: Int,
        @SerializedName("out") val out: ArrayList<Any>,
        @SerializedName("code") val code: Int,
        @SerializedName("exe_time") val exeTime: Int,
    )
}
