package com.github.miwu.logic.model.user

data class SidMsg(
    val qs: String,
    val sid: String,
    val sign: String,
    val callback: String,
)
