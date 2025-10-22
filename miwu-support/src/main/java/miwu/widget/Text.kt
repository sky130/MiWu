package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Bind<Property>("temperature-humidity-sensor", "temperature")
@Bind<Property>("environment", "relative-humidity")
@Bind<Property>("environment", "pm2.5-density")
@Bind<Property>("gas-sensor", "gas-concentration")
@Header
class Text : MiwuWidget<String>()
