package com.github.miwu.miot.utils

fun getUnitString(unit: String) = when (unit) {
    "celsius" -> "°C"
    "percentage" -> "%"
    else -> unit
}