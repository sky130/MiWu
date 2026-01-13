package miwu.miot.kmp.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunScene(
    @SerialName("us_id")
    val usId: Long,
    @SerialName("key")
    val key: String = "",
)