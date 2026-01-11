package miwu.miot.model

data class MiotUser(
    val userId: String,
    val securityToken: String,
    val serviceToken: String,
    val deviceId: String,
)