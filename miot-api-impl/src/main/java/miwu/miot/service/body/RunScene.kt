package miwu.miot.service.body

import com.google.gson.annotations.SerializedName

data class RunScene(
    @SerializedName("us_id")
    val usId: Long,
    @SerializedName("key")
    val key: String = "",
)