package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("air-conditioner")
@Property("target-temperature")
@SubHeader
class DoubleValueController: MiwuWidget<Int>()