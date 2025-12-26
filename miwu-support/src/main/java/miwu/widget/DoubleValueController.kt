package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@SubHeader
@Bind<Property>("air-conditioner", "target-temperature")
class DoubleValueController: MiwuWidget<Int>()
