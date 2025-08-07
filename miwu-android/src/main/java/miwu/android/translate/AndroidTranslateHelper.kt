package miwu.android.translate

import kndroidx.extension.string
import miwu.android.R
import miwu.support.translate.TranslateHelper

object AndroidTranslateHelper : TranslateHelper {
    override fun translate(origin: String) = when (origin) {
        "Auto" -> R.string.Auto.string
        "Cool" -> R.string.Cool.string
        "Dry" -> R.string.Dry.string
        "Heat" -> R.string.Heat.string
        "Fan" -> R.string.Fan.string
        "Normal" -> R.string.Normal.string
        "Low" -> R.string.LowFood.string
        "Empty" -> R.string.EmptyFood.string
        "Temperature" -> R.string.Temperature.string
        "Relative Humidity" -> R.string.relative_humidity.string
        "Battery" -> R.string.battery.string
        "Pet Food Out" -> R.string.pet_food_out.string
        else -> origin
    }
}