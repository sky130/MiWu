package miwu.device

import miwu.annotation.Device
import miwu.annotation.Widgets
import miwu.support.MiwuDevice
import miwu.layout.*
import miwu.widget.*

@Device("airer")
@Widgets(
    StatusText::class,
    AirerLayout::class,
    SwitchBar::class
)
class Airer : MiwuDevice()