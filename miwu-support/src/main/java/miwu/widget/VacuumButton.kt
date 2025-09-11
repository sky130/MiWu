package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("vacuum", "battery")
@Action(
    "start-sweep", // "stop-sweeping"
    "start-charge",
)
@Body
class VacuumButton : MiwuWidget<Unit>() {
    override val icon get() = Icons.mapTo(actionName)
}