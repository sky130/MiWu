package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("light")
@Widgets(Switch::class, ColorTemperatureSeekbar::class, IntSeekbar::class)
class Light : MiwuDevice()