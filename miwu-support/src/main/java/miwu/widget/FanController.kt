package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.Header
import miwu.support.MiwuWidget
import miwu.support.icon.Icon

@Widget
@Header
@Bind<Property>("fan", "fan-level")
@Bind<Property>("dehumidifier", "fan-level")
@ValueList(pointTo = FanLevelController::class)
class FanController : MiwuWidget<Int>() {
    override val icon: Icon get() = super.icon
}
