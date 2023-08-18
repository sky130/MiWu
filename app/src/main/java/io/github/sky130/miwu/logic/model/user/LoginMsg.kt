package io.github.sky130.miwu.logic.model.user

data class LoginMsg(
    val isError: Boolean,
    var code: String,
    var message: String,
    var sid: String = "",
    var userId: String = "",
    var securityToken: String = "",
    var deviceId: String = "",
    var serviceToken: String = "",
)
