package miot.kotlin.model.att

import com.google.gson.annotations.SerializedName

data class DeviceAtt(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<Att>?,
){
    data class Att(
        @SerializedName("did") val did: String,
        @SerializedName("iid") val iid: String,
        @SerializedName("siid") val siid: Int,
        @SerializedName("piid") val piid: Int,
        @SerializedName("value") val value: Any?,
        @SerializedName("code") val code: Int,
        @SerializedName("updateTime") val updateTime: Long,
        @SerializedName("exe_time") val exeTime: Int,
    )
}
