package miwu.miot.model

import kotlinx.serialization.Serializable

@Serializable
data class MiotUser(
    val userId: String,
    val cUserId: String,
    val nonce: String,
    val ssecurity: String,
    val psecurity: String,
    val passToken: String,
    val serviceToken: String,
    val deviceId: String,
)