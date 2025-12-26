package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("air-conditioner")
@Widgets(
    SwitchButton::class,
    ModeButton::class,
    DoubleValueController::class
)
class AirConditioner : MiwuDevice()