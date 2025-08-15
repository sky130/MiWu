package miwu.android.translate

import kndroidx.extension.string
import miwu.android.R
import miwu.support.translate.TranslateHelper

object AndroidTranslateHelper : TranslateHelper {
    override fun translate(origin: String) = when (origin) {
        "Auto" -> R.string.Auto
        "Cool" -> R.string.Cool
        "Dry" -> R.string.Dry
        "Heat" -> R.string.Heat
        "Fan" -> R.string.Fan
        "Normal" -> R.string.Normal
        "Low" -> R.string.LowFood
        "Empty" -> R.string.EmptyFood
        "Temperature" -> R.string.Temperature
        "Relative Humidity" -> R.string.relative_humidity
        "Battery" -> R.string.battery
        "Pet Food Out" -> R.string.pet_food_out
        "fan" -> R.string.fan
        else -> origin
    }.let {
        when (it) {
            is String -> it
            is Int -> it.string
            else -> throw IllegalArgumentException("Unknown type: $it")
        }
    }
}