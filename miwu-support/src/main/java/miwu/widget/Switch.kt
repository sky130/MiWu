package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("light")
@Property("on")
@Header
class Switch : MiwuWidget<Boolean>(){
    override val icon get() = Icons.Power
}
