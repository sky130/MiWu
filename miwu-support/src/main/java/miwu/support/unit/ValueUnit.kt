package miwu.support.unit

/**
 * @author Sky233
 *
 * 计量单位的翻译工具
 */
object ValueUnit {

    /**
     * 将计量单位转换为翻译后的计量单位
     */
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

    /**
     * 摄氏度
     */
    const val Celsius  = "celsius"

    /**
     * 华氏度
     */
    const val Fahrenheit = "fahrenheit"

    /**
     * 秒
     */
    const val Second = "seconds"

    /**
     * 开尔文
     */
    const val Kelvin = "kelvin"

    /**
     * 百分比
     */
    const val Percentage = "percentage"

    /**
     * 小时
     */
    const val Hours = "hours"

    /**
     * 分钟
     */
    const val Minutes = "minutes"

    /**
     * 帕斯卡
     */
    const val Pascal = "pascal"

    /**
     * 弧角度
     */
    const val Arcdegrees = "arcdegrees"
}