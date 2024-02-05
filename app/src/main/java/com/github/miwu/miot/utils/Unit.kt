package com.github.miwu.miot.utils

fun getUnitString(unit: String) = when (unit) {
    "celsius" -> "°C"
    "percentage" -> "%"
    "seconds" -> "s"
    "kelvin" -> "K"
    "hours" -> "h"
    "minutes" -> "min"
    "pascal" -> "Pa"
    "arcdegrees" -> "Rad"
    else -> unit
}