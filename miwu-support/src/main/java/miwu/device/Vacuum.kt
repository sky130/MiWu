package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Device("vacuum")
@Widgets(
    VacuumButton::class,
    StatusText::class
)
class Vacuum : MiwuDevice()