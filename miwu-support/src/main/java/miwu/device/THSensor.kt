package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("temperature-humidity-sensor")
@Widgets(Text::class)
class THSensor : MiwuDevice()