package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Body
@Bind<Property>("air-conditioner", "mode")
@Bind<Property>("air-purifier", "mode")
@ValueList
class ModeButton : MiwuWidget<Int>() {
    // warning 这里一定要用 getter 写法, 不可以直接赋值
    override val icon get() = Icons.mapTo(description)
}