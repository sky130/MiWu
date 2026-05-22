package miwu.device

import miwu.annotation.Device
import miwu.annotation.Widgets
import miwu.support.MiwuDevice
import miwu.widget.FanController
import miwu.widget.FanLevelController
import miwu.widget.ModeButton
import miwu.widget.NumberValueController
import miwu.widget.SwitchBar
import miwu.widget.Text

@Device("dehumidifier")
@Widgets(
    Text::class,
    ModeButton::class,
    NumberValueController::class,
    FanController::class,
    SwitchBar::class,
)
class Dehumidifier : MiwuDevice()