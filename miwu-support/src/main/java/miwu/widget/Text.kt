package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service("temperature-humidity-sensor", "environment")
@Property("temperature", "relative-humidity", "pm2.5-density")
@Header
class Text : MiwuWidget<String>()