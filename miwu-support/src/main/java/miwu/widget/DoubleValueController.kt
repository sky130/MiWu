package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuWidget

@Widget
@ControlPanel
// @Bind<Property>("air-conditioner", "target-temperature")
class DoubleValueController : MiwuWidget<Int>()
