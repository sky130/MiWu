package miwu.device

import miwu.annotation.Device
import miwu.annotation.Widgets
import miwu.support.base.MiwuDevice
import miwu.widget.*

@Device("camera")
@Widgets(
    WidgetSwitch::class
)
class Camera : MiwuDevice()

