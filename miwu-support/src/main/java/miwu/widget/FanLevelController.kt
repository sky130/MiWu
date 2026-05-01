package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.Header
import miwu.support.MiwuWidget

@Widget
@Header
@Bind<Property>("*", "*")
class FanLevelController : MiwuWidget<Int>()