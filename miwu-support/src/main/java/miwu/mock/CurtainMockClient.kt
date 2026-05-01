package miwu.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.spec.MiotSpec.Property
import miwu.spec.MiotSpec.Service
import miwu.support.mock.MockMiotDeviceClient

class CurtainMockClient(
    mockScope: CoroutineScope,
    specAtt: SpecAtt,
    device: MiotDevice,
) : MockMiotDeviceClient(mockScope, specAtt, device) {

    override fun onInit() {
        val curtainStatus = Service.Curtain with Property.Status
        val curtainStatusProperty = getProperty(Service.Curtain, Property.Status)
        val stopValue = curtainStatusProperty?.firstValueOrNull { it == STOP }?.value

        update(curtainStatus, stopValue)

        registerProperty(
            Service.Curtain,
            Property.MotorControl,
        ) { property, store, input ->
            val desc: String? = property.valueList
                ?.firstOrNull { it.value == input }
                ?.description
                ?.let { desc ->
                    when (desc) {
                        OPEN -> OPENING
                        CLOSE -> CLOSING
                        PAUSE -> STOP
                        else -> {
                            when (store[curtainStatus] as Int) {
                                0 -> CLOSING
                                1 -> OPENING
                                else -> STOP
                            }
                        }
                    }
                }
            val value = curtainStatusProperty?.firstValueOrNull { it == desc }?.value
            update(curtainStatus, value)
            if (value != stopValue) {
                delay(3000)
                update(curtainStatus, stopValue)
            }
        }
    }

    companion object {
        // 0 - Open 打开
        // 1 - Close 关闭
        // 2 - Pause 暂停
        // 3 - Toggle 开/停/关
        private const val OPEN = "Open"
        private const val CLOSE = "Close"
        private const val PAUSE = "Pause"
        private const val TOGGLE = "Toggle"

        // 0 - Opening 打开中
        // 1 - Closing 关闭中
        // 2 - Stop 停止
        private const val OPENING = "Opening"
        private const val CLOSING = "Closing"
        private const val STOP = "Stop"
    }
}