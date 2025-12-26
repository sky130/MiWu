package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@SubHeader
@Bind<Property>("light", "brightness")
class IntSeekbar : MiwuWidget<Int>() {
    override val icon get() = Icons.mapTo(propertyName)
}
