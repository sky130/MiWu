package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.Header
import miwu.annotation.widget.SubHeader
import miwu.support.base.MiwuWidget
import miwu.support.icon.Icon

@Widget
@Header
@Bind<Property>("fan", "fan-level")
@ValueList(FanLevelController::class)
class FanController : MiwuWidget<Int>() {
    override val icon: Icon get() = super.icon
}
