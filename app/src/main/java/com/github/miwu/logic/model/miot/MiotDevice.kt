package com.github.miwu.logic.model.miot

data class MiotDevice(
    val type: String,
    val description: String,
    val services: ArrayList<MiotService>,
)