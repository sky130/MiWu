package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("light")
@Widgets(
    ColorTemperatureSeekbar::class,
    IntSeekbar::class,
    FanController::class,
    FanLevelController::class,
    WidgetSwitch::class
)
class Light : MiwuDevice()