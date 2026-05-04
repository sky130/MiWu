package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.support.mock.DefaultMockMiotDeviceClient
import miwu.widget.*

@Device("air-conditioner")
@Widgets(
    SwitchButton::class,
    ModeButton::class,
    NumberValueController::class
)
class AirConditioner : MiwuDevice()