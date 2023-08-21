package io.github.sky130.miwu.logic.model.miot

data class MiotService(
    val iid: Int,
    val type: String,
    val description: String,
    val properties: ArrayList<MiotProperties>,
)
