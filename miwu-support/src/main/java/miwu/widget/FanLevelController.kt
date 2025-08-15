package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.Header
import miwu.annotation.widget.SubHeader
import miwu.support.base.MiwuWidget

@Widget
@Service("*")
@Property("*")
@Header
class FanLevelController : MiwuWidget<Int>()