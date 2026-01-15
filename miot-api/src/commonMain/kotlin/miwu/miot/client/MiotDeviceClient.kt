package miwu.miot.client

import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice

interface MiotDeviceClient {
    /**
     * 获取设备属性
     * @param device
     * @param att 需要获取的属性
     */
    suspend fun get(device: MiotDevice, att: Array<out GetAtt>): Result<DeviceAtt>

    /**
     * 设置设备属性
     * @param device
     * @param att 需要设置的属性
     */
    suspend fun set(device: MiotDevice, att: Array<out SetAtt>): Result<Unit>

    /**
     * 执行动作
     * @param device
     * @param siid ServiceIID
     * @param aiid ActionIID
     * @param input
     */
    suspend fun action(
        device: MiotDevice, siid: Int, aiid: Int, vararg input: Any
    ): Result<Any?>
}