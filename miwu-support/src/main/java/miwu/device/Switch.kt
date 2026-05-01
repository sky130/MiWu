package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("switch")
@Widgets(
    SwitchButton::class
)
class Switch : MiwuDevice()