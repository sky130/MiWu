package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("fan")
@Widgets(
    FanController::class,
    FanLevelController::class,
    WidgetSwitch::class,
)
class Fan : MiwuDevice()