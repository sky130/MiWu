package com.github.miwu.logic.model.miot

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class MiotProperties(
    val iid: Int,
    val type: String,
    val format: String,
    val access: ArrayList<String>,
    val unit: String?,
    @SerializedName("value-range")
    val valueRange: ArrayList<Int>?,
    @SerializedName("value-list")
    val valueList: ArrayList<PropertiesValue>?,
)

data class PropertiesValue(val value: Int, val description: String)
