package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Body
@Bind<Action>("battery", "start-charge")
@Bind<Action>("vacuum", "start-sweep")
// @Bind<Action>("vacuum", "stop-sweeping")
class VacuumButton : MiwuWidget<Unit>() {
    override val icon get() = Icons.mapTo(actionName)
}