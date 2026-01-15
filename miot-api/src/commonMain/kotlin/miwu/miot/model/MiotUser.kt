package miwu.miot.model

import kotlinx.serialization.Serializable

@Serializable
data class MiotUser(
    val userId: String,
    val securityToken: String,
    val serviceToken: String,
    val deviceId: String,
)