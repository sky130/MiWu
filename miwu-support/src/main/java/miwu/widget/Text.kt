package miwu.widget

import miwu.annotation.*
import miwu.annotation.widget.*
import miwu.support.base.MiwuWidget

@Widget
@Service(
    "temperature-humidity-sensor",
    "environment",
    "environment",
    "gas-sensor"
)
@Property(
    "temperature",
    "relative-humidity",
    "pm2.5-density",
    "gas-concentration"
)
@Header
class Text : MiwuWidget<String>()