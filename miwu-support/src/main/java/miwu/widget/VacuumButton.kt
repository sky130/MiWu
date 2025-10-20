package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Action>("battery", "start-charge")
@Bind<Action>("vacuum", "start-sweep")
// @Bind<Action>("vacuum", "stop-sweeping")
@Body
class VacuumButton : MiwuWidget<Unit>() {
    override val icon get() = Icons.mapTo(actionName)
}