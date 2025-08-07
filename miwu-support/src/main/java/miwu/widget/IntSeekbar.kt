package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("light")
@Property("brightness")
@SubHeader
class IntSeekbar : MiwuWidget<Int>() {
    override val icon get() = Icons.mapTo(description)
}