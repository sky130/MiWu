package miwu.layout

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuLayout

@Widget
@SubHeader
@Bind<Property>("curtain", "motor-control")
class CurtainLayout : MiwuLayout<Int>()