package miot.kotlin.service.body


import com.google.gson.annotations.SerializedName

data class GetHome(
    @SerializedName("app_ver") val appVer: Int = 0,
    @SerializedName("fetch_share") val fetchShare: Boolean = false,
    @SerializedName("fetch_share_dev") val fetchShareDev: Boolean = false,
    @SerializedName("fg") val fg: Boolean = false,
    @SerializedName("limit") val limit: Int = 0
)