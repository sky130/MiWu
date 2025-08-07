package miwu.miot.service.body


import com.google.gson.annotations.SerializedName

data class GetHome(
    @SerializedName("app_ver") val appVer: Int = 7,
    @SerializedName("fetch_share") val fetchShare: Boolean = true,
    @SerializedName("fetch_share_dev") val fetchShareDev: Boolean = true,
    @SerializedName("fg") val fg: Boolean = false,
    @SerializedName("limit") val limit: Int = 300
)