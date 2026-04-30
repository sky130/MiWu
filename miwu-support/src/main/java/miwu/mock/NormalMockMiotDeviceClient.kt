package miwu.mock

import kotlinx.coroutines.CoroutineScope
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice

class NormalMockMiotDeviceClient(
    mockScope: CoroutineScope,
    specAtt: SpecAtt,
    device: MiotDevice,
) : MockMiotDeviceClient(mockScope, specAtt, device) {
    override fun onInit() = Unit
}