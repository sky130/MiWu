package com.github.miwu.logic.model.device

data class SetDeviceATT(
    val code: String,
    val iid: String,
    val siid: Int,
    val piid: Int,
    val exeTime: Int,
)