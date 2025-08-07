package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("light")
@Property("color-temperature")
@SubHeader
class ColorTemperatureSeekbar : MiwuWidget<Int>() // 只适用于灯泡色温, 因为进度条比较特殊, 无复用性
