package miwu.support.mock.base

import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.client.MiotDeviceClient
import miwu.miot.model.MiotResponse
import miwu.miot.model.att.PropertyResponse
import miwu.miot.model.miot.MiotDevice

/**
 * 如果想要实现 MockMiotDeviceClient 就必须继承该类去实现,
 * 用于模拟设备的属性获取和设置, onAction 可以视情况继承实现, 在需要返回数据的时候就实现
 *
 * @see [MiotDeviceClient]
 */
abstract class BaseMockMiotDeviceClient(val miotDevice: MiotDevice) : MiotDeviceClient {
    abstract suspend fun onGet(att: Array<out GetAtt>): Result<MiotResponse<PropertyResponse>>

    abstract suspend fun onSet(att: Array<out SetAtt>): Result<Unit>

    open suspend fun onAction(siid: Int, aiid: Int, vararg input: Any): Result<Any?> =
        runCatching { }

    override suspend fun get(
        device: MiotDevice,
        att: Array<out GetAtt>
    ): Result<MiotResponse<PropertyResponse>> = onGet(att)

    override suspend fun set(
        device: MiotDevice,
        att: Array<out SetAtt>
    ): Result<Unit> = onSet(att)

    override suspend fun action(
        device: MiotDevice,
        siid: Int,
        aiid: Int,
        vararg input: Any
    ): Result<Any?> = onAction(siid, aiid, input)
}