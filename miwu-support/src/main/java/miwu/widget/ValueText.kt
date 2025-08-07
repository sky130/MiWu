package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("environment")
@Property("air-purifier")
@Header
class ValueText : MiwuWidget<Int>()