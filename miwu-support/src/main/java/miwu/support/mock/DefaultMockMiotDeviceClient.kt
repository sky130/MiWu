package miwu.support.mock

import kotlinx.coroutines.CoroutineScope
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.miot.MiotDevice

class DefaultMockMiotDeviceClient(
    mockScope: CoroutineScope,
    specAtt: SpecAtt,
    device: MiotDevice,
) : MockMiotDeviceClient(mockScope, specAtt, device) {

    /**
     * 兼容 [MockMiotDeviceClient] 的构造函数
     */
    constructor(
        deviceType: String,
        mockScope: CoroutineScope,
        specAtt: SpecAtt,
        device: MiotDevice,
    ) : this(mockScope, specAtt, device)

    override fun onInit() = Unit
}