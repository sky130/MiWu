package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("environment", "vacuum", "curtain")
@Property("air-purifier", "status", "mode")
@Header
class ValueText : MiwuWidget<Int>()