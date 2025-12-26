package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.Header
import miwu.annotation.widget.SubHeader
import miwu.support.base.MiwuWidget

@Widget
@Header
@Bind<Property>("*", "*")
class FanLevelController : MiwuWidget<Int>()