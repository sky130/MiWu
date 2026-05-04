package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.layout.*
import miwu.mock.CurtainMockClient
import miwu.widget.*

@Device("curtain")
@Widgets(
    StatusText::class,
    CurtainLayout::class
)
@Mock(CurtainMockClient::class)
class Curtain : MiwuDevice()