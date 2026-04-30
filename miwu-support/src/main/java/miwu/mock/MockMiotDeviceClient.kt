package miwu.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.mock.base.BaseMockMiotDeviceClient

typealias MockStore = Map<Int, MutableMap<Int, Any>>
typealias MockAction = MutableMap<Int, MutableMap<Int, MockActionHook>>
typealias MockActionHook = (store: MockStore, input: Array<out Any>) -> Any
typealias MockProperty = MutableMap<Int, MutableMap<Int, MockPropertyHook>>
typealias MockPropertyHook = suspend (store: MockStore, origin: Any) -> Unit

typealias MockMiotDeviceClientBuilder = (mockScope: CoroutineScope, specAtt: SpecAtt, device: MiotDevice) -> MockMiotDeviceClient

/**
 * 用于在测试中模拟 [miwu.miot.client.MiotDeviceClient] 行为的抽象基类。
 *
 * 它基于 [specAtt] 自动初始化一个存放设备属性值的内部存储 [mockStore]，
 * 并通过 [registerProperty] 与 [registerAction] 提供可插拔的钩子机制，
 * 以便测试者控制设备的属性读写与动作执行行为，无需依赖真实设备。
 *
 * 内部行为简述:
 * - 属性读取 ([onGet]): 直接返回 [mockStore] 中对应 `siid/piid` 的当前值
 * - 属性写入 ([onSet]):
 *   如果未注册对应的 [MockPropertyHook]，则同步更新 [mockStore]
 *   否则取消该属性上一个未完成的 Job，并在 [mockScope] 中启动新的协程执行钩子
 *   钩子可以自由读写 [mockStore] 以实现延迟写入、条件写入等异步行为
 * - 动作执行 ([onAction]): 调用对应 `siid/aiid` 注册的 [MockActionHook] 并返回其结果；
 *   若未注册则返回成功但数据为空的 [Result]
 *
 * 基类设计思路：
 * 部分设备属性在受控后并非立即稳定，而是存在异步过渡状态。
 * 以窗帘电机（如 [miwu.device.Curtain]）为例：
 * - 开窗时，电机先进入“开窗中”，待到位后自动转为“停止”；
 * - 关窗时，电机先进入“关窗中”，待到位后自动转为“停止”。
 * 为模拟此类延迟自更新行为，`MockPropertyHook` 在协程中执行，
 * 允许测试在钩子内通过 `mockScope` 延迟写入 `mockStore`。
 *
 * @param mockScope 用于执行模拟协程的 [CoroutineScope]，例如在 [MockPropertyHook] 中延迟更新属性
 * @param specAtt 设备的规格信息，用于初始化默认属性值及注册钩子时的名称查找
 * @param device 模拟的设备实例
 */
abstract class MockMiotDeviceClient(
    val mockScope: CoroutineScope,
    val specAtt: SpecAtt,
    device: MiotDevice,
) : BaseMockMiotDeviceClient(device) {
    private val mockStore: MockStore =
        specAtt.services.associate { service ->
            service.properties
                .orEmpty()
                .associate { it.iid to it.getDefaultValue() }
                .toMutableMap()
                .let { service.iid to it }
        }
    private val mockAction: MockAction = mutableMapOf()
    private val mockProperty: MockProperty = mutableMapOf()
    private val mockJob: MutableMap<Pair<Int, Int>, Job> = mutableMapOf()
    abstract fun onInit()

    /**
     * 注册一个 MockProperty
     */
    fun registerProperty(
        serviceName: String,
        propertyName: String,
        property: MockPropertyHook
    ) {
        val siid: Int
        val aiid: Int
        specAtt.services
            .firstOrNull { it.type == serviceName }
            ?.also { siid = it.iid }
            ?.actions
            ?.firstOrNull { it.type == propertyName }
            ?.also { aiid = it.iid }
            ?: return
        mockProperty.getOrPut(siid) { mutableMapOf() }[aiid] = property
    }

    /**
     * 注册一个 MockAction
     */
    fun registerAction(
        serviceName: String,
        actionName: String,
        action: MockActionHook
    ) {
        val siid: Int
        val aiid: Int
        specAtt.services
            .firstOrNull { it.type == serviceName }
            ?.also { siid = it.iid }
            ?.actions
            ?.firstOrNull { it.type == actionName }
            ?.also { aiid = it.iid }
            ?: return
        mockAction.getOrPut(siid) { mutableMapOf() }[aiid] = action
    }

    override suspend fun onGet(att: Array<out GetAtt>): Result<DeviceAtt> =
        runCatching {
            DeviceAtt(
                code = 0,
                message = "",
                result = att.map { info ->
                    DeviceAtt.Att(
                        did = miotDevice.did,
                        iid = "",
                        siid = info.first,
                        piid = info.second,
                        value = mockStore[info.first]?.get(info.second),
                        code = 0,
                        updateTime = null,
                        exeTime = 0
                    )
                }.let { ArrayList(it) }
            )
        }

    override suspend fun onSet(att: Array<out SetAtt>): Result<Unit> =
        runCatching {
            for (entry in att) {
                val mockFun = mockProperty[entry.siid]?.get(entry.piid)
                if (mockFun == null) {
                    mockStore[entry.siid]?.set(entry.piid, entry.value)
                    continue
                }
                val newJob = mockScope.launch {
                    mockFun(mockStore, entry.value)
                }.apply {
                    invokeOnCompletion {
                        mockJob.remove(entry.siid to entry.piid)
                    }
                }
                mockJob[entry.siid to entry.piid]?.cancel()
                mockJob[entry.siid to entry.piid] = newJob
            }
        }

    override suspend fun onAction(siid: Int, aiid: Int, vararg input: Any): Result<Any?> =
        runCatching {
            mockAction[siid]?.get(aiid)?.invoke(mockStore, input)
        }
}