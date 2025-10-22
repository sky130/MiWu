package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("air-conditioner", "target-temperature")
@SubHeader
class DoubleValueController: MiwuWidget<Int>()
