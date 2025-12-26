package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

typealias WidgetSwitch = Switch

@Widget
@Header
@Bind<Property>("light", "on")
@Bind<Property>("fan", "on")
@Bind<Property>("camera", "on")
class Switch : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}
