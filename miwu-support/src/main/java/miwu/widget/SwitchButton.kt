package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("air-conditioner", "on")
@Bind<Property>("air-purifier", "on")
@Body
class SwitchButton : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}