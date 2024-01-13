package miot.kotlin.service.body

import com.google.gson.annotations.SerializedName

data class GetUserInfo(
    @SerializedName("id") val id: String,
)
