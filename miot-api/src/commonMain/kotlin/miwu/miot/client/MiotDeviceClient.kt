package miwu.miot.client

import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.MiotUser

/**
 * 与 MIoT (小米物联网) 设备交互的客户端接口。
 *
 * 通常通过依赖注入框架（如 [Koin]）获取实例。使用时需要提供有效的 [MiotUser] 身份凭证
 *
 * 下面是使用 Koin 来进行依赖注入的例子
 *
 * ``` kt
 * val user: MiotUser = ...
 * val client: MiotDeviceClient = get<MiotDeviceClient> { parametersOf(user) }
 * val device: MiotDevice = ...
 * val att: GetAtt = 1 to 2 // siid to piid
 * val result = client.get(device, att).getOrThrow()
 * for (item in result) {
 *     println("${item.siid} ${item.piid} ${item.value}")
 * }
 * ```
 *
 * @see [MiotUser] 用户身份信息
 * @see [MiotDevice] 设备标识符
 * @see [GetAtt] 属性请求结构
 * @see [DeviceAtt] 属性响应数据
 *
 */
interface MiotDeviceClient {
    /**
     * 异步获取指定的设备属性。
     *
     * @param device 目标设备 [MiotDevice]。
     * @param att 需要获取的属性数组，每个元素都是一个 [GetAtt] 实例。
     * @return 返回一个 [Result] 对象，成功时包含设备属性 [DeviceAtt]，失败时则包含异常信息。
     */
    suspend fun get(device: MiotDevice, att: Array<out GetAtt>): Result<DeviceAtt>

    /**
     * 异步设置指定的设备属性。
     *
     * @param device 目标设备 [MiotDevice]。
     * @param att 需要设置的属性数组，每个元素都是一个 [SetAtt] 实例。
     * @return 返回一个 [Result] 对象，操作成功时返回 [Unit]，失败时则包含异常信息。
     */
    suspend fun set(device: MiotDevice, att: Array<out SetAtt>): Result<Unit>

    /**
     * 在设备上异步执行一个操作 (action)。
     *
     * @param device 目标设备 [MiotDevice]。
     * @param siid 服务的实例 ID (ServiceIID)。
     * @param aiid 操作的实例 ID (ActionIID)。
     * @param input 执行该操作所需的输入参数列表。
     * @return 返回一个 [Result] 对象，成功时可能会根据 `Action` 情况返回 `output`，失败时则包含异常信息。
     */
    suspend fun action(
        device: MiotDevice, siid: Int, aiid: Int, vararg input: Any
    ): Result<Any?>
}
