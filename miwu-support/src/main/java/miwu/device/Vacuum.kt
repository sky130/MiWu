package miwu.device

import miwu.support.base.MiwuDevice
import miwu.annotation.*
import miwu.widget.*

@Widgets(VacuumButton::class, StatusText::class)
@Device("vacuum")
class Vacuum : MiwuDevice()