package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("fan")
@Widgets(Switch::class, FanController::class, FanLevelController::class)
class Fan : MiwuDevice()