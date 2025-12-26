package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@SubHeader
@Bind<Property>("curtain", "motor-control")
class CurtainLayout : MiwuWidget<Unit>()