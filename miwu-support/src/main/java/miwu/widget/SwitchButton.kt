package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Body
@Bind<Property>("air-conditioner", "on")
@Bind<Property>("air-purifier", "on")
class SwitchButton : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}