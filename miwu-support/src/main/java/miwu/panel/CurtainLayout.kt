package miwu.panel

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuPanel

@Widget
@SubHeader
@Bind<Property>("curtain", "motor-control")
class CurtainLayout : MiwuPanel<Int>()