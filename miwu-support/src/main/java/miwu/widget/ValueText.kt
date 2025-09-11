package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("environment", "vacuum")
@Property("air-purifier", "status")
@Header
class ValueText : MiwuWidget<Int>()