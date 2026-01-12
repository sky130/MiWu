package miwu.miot.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetHome(
    @SerialName("app_ver")
    var appVer: Int = 7,
    @SerialName("fetch_share")
    var fetchShare: Boolean = true,
    @SerialName("fetch_share_dev")
    var fetchShareDev: Boolean = true,
    @SerialName("fg")
    var fg: Boolean = false,
    @SerialName("limit")
    var limit: Int = 300
)