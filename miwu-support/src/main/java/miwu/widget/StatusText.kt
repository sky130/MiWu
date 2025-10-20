package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("environment", "status")
@Bind<Property>("vacuum", "mode")
@Bind<Property>("curtain", "status")
@Bind<Property>("gas-sensor", "status")
@Header
class StatusText : MiwuWidget<Int>()