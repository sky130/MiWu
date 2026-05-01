package miwu.layout

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuLayout

@Widget
@SubHeader
@Bind<Property>("curtain", "motor-control")
class CurtainLayout : MiwuLayout<Int>() {
    override val isMultiAttribute = true
}