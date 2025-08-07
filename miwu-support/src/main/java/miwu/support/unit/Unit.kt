package miwu.support.unit

object Unit {
    fun from(unit: String) = when (unit) {
        Celsius -> "°C"
        Fahrenheit -> "°F"
        Second -> "s"
        Kelvin -> "K"
        Percentage -> "%"
        Hours -> "h"
        Minutes -> "min"
        Pascal -> "Pa"
        Arcdegrees -> "Rad"
        else -> unit
    }

    const val Celsius  = "celsius"
    const val Fahrenheit = "fahrenheit"
    const val Second = "seconds"
    const val Kelvin = "kelvin"
    const val Percentage = "percentage"
    const val Hours = "hours"
    const val Minutes = "minutes"
    const val Pascal = "pascal"
    const val Arcdegrees = "arcdegrees"
}