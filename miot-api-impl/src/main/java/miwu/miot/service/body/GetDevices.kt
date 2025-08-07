package miwu.miot.service.body


import com.google.gson.annotations.SerializedName

data class GetDevices(
    @SerializedName("home_owner")
    val homeOwner: Long,
    @SerializedName("home_id")
    val homeId: Long,
    @SerializedName("limit")
    val limit: Int = 200
)