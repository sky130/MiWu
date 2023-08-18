package io.github.sky130.miwu.logic.model.miot

data class MiotDevice(
    val type: String,
    val description: String,
    val services: ArrayList<MiotService>,
)