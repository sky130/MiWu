package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("curtain")
@Property("motor-control")
@SubHeader
class CurtainLayout : MiwuWidget<Unit>()