package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("fan")
@Widgets(
    FanController::class,
    FanLevelController::class,
    SwitchBar::class,
)
class Fan : MiwuDevice()