package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("switch")
@Widgets(
    SwitchButton::class
)
class Switch : MiwuDevice()