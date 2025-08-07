package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("air-purifier")
@Widgets(Text::class, ValueText::class, SwitchButton::class, ModeButton::class)
class AirPurifier : MiwuDevice()