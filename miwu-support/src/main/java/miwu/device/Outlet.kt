package miwu.device

import miwu.annotation.Device
import miwu.annotation.Widgets
import miwu.support.MiwuDevice
import miwu.widget.SwitchBar
import miwu.widget.Text

@Device("outlet")
@Widgets(
    SwitchBar::class,
    Text::class
)
class Outlet : MiwuDevice()