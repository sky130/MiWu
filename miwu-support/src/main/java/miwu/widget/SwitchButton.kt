package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("air-conditioner", "air-purifier")
@Property("on")
@Body
class SwitchButton : MiwuWidget<Boolean>() {
    override val icon get() = Icons.Power
}