package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("light", "brightness")
@SubHeader
class IntSeekbar : MiwuWidget<Int>() {
    override val icon get() = Icons.mapTo(propertyName)
}
