package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuWidget

@Widget
@HeaderPanel
@Bind<Property>("light", "on")
@Bind<Property>("fan", "on")
@Bind<Property>("camera", "on")
@Bind<Property>("dehumidifier", "on")
class SwitchBar : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}
