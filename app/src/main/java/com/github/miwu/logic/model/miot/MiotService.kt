package com.github.miwu.logic.model.miot

data class MiotService(
    val iid: Int,
    val type: String,
    val description: String,
    val properties: ArrayList<MiotProperties>,
)
