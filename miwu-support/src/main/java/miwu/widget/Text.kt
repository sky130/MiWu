package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.MiwuWidget

@Widget
@Header
@Bind<Property>("temperature-humidity-sensor", "temperature")
@Bind<Property>("temperature-humidity-sensor", "relative-humidity")
@Bind<Property>("environment", "relative-humidity")
@Bind<Property>("environment", "pm2.5-density")
@Bind<Property>("environment", "temperature")
@Bind<Property>("gas-sensor", "gas-concentration")
@Bind<Property>("power-consumption", "power-consumption")
@Bind<Property>("power-consumption", "electric-current")
@Bind<Property>("power-consumption", "voltage")
@Bind<Property>("power-consumption", "voltage")
class Text : MiwuWidget<String>()