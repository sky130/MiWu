package miwu.layout

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuLayout

@Widget
@ControlPanel
@Bind<Property>("airer", "motor-control")
class AirerLayout : MiwuLayout<Int>() {
    override val isMultiAttribute = true
}