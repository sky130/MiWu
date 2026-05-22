package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuWidget

@Widget
@ControlPanel
@Bind<Property>("light", "brightness")
class IntSeekbar : MiwuWidget<Int>() {
    override val icon get() = Icons.mapTo(propertyName)
}
