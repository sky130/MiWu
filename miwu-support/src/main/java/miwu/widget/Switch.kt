package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("light", "on")
@Bind<Property>("fan", "on")
@Header
class Switch : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}
