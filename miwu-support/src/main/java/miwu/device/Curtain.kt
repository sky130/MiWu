package miwu.device

import miwu.support.MiwuDevice
import miwu.annotation.*
import miwu.layout.CurtainLayout
import miwu.mock.CurtainMockClient
import miwu.support.mock.DefaultMockMiotDeviceClient

@Device("curtain")
@Widgets(CurtainLayout::class)
@Mock(CurtainMockClient::class)
class Curtain : MiwuDevice()