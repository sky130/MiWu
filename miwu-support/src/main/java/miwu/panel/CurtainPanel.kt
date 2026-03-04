package miwu.panel

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuPanel
import miwu.widget.ModeButton
import miwu.widget.StatusButton

@Widget
@Body
@Bind<Property>("curtain", "motor-control")
class CurtainPanel : MiwuPanel<Int>() {
    override fun onCreateWidget() = scope {
        val pause = "Pause"
        val special = valueList.first { it.description == pause }
        valueList
            .filterNot { it.description == pause }
            .forEach { value ->
                create<StatusButton> {
                    defaultValue(value, special)
                }
            }
    }
}