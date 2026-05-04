package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuWidget

@Widget
@Header
@Bind<Property>("environment", "status")
@Bind<Property>("vacuum", "mode")
@Bind<Property>("curtain", "status")
@Bind<Property>("gas-sensor", "status")
@Bind<Property>("airer", "status")
class StatusText : MiwuWidget<Int>()