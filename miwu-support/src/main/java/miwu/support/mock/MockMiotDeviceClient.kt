package miwu.support.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import miwu.annotation.basic.MockClient
import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.model.MiotResponse
import miwu.miot.model.att.Property
import miwu.miot.model.att.PropertyList
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.spec.SpecAction
import miwu.miot.model.spec.SpecProperty
import miwu.miot.utils.runCatchingSuspend
import miwu.support.mock.base.BaseMockMiotDeviceClient

/**
 * 模拟设备属性值的存储，使用 `(siid, piid)` 作为属性索引。
 */
typealias MockStore = MutableMap<Pair<Int, Int>, Any>

/**
 * 模拟动作钩子的注册表，使用 `(siid, aiid)` 作为动作索引。
 */
typealias MockAction = MutableMap<Pair<Int, Int>, MockActionHook>

/**
 * 模拟动作执行时调用的钩子。
 *
 * @param action 要执行的动作规格
 * @param store 当前设备的模拟属性存储，可用于读取或更新属性值
 * @param input 动作输入参数
 * @return 动作执行结果
 */
typealias MockActionHook = (action: SpecAction, store: MockStore, input: Array<out Any>) -> Any

/**
 * 模拟属性钩子的注册表，使用 `(siid, piid)` 作为属性索引。
 */
typealias MockProperty = MutableMap<Pair<Int, Int>, MockPropertyHook>

/**
 * 模拟属性写入时调用的挂起钩子。
 *
 * @param property 被写入的属性规格
 * @param store 当前设备的模拟属性存储，可用于延迟或条件更新属性值
 * @param origin 请求写入的原始值
 */
typealias MockPropertyHook = suspend (property: SpecProperty, store: MockStore, origin: Any) -> Unit

/**
 * 创建模拟设备客户端的构建函数。
 *
 * @param deviceType 设备类型标识
 * @param mockScope 用于执行模拟协程的作用域
 * @param specAtt 设备规格信息
 * @param device 要模拟的设备实例
 * @return 创建完成的模拟设备客户端
 */
typealias MockMiotDeviceClientBuilder = (deviceType: String, mockScope: CoroutineScope, specAtt: SpecAtt, device: MiotDevice) -> BaseMockMiotDeviceClient

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
) : BaseMockMiotDeviceClient(device), MockClient {
    private val mockStore: MockStore =
        specAtt.services.flatMap { service ->
            service.properties
                .orEmpty()
                .map { (service.iid to it.iid) to it.getDefaultValue() }
        }.toMap().toMutableMap()
    private val mockAction: MockAction = mutableMapOf()
    private val mockProperty: MockProperty = mutableMapOf()
    private val mockJob: MutableMap<Pair<Int, Int>, Job> = mutableMapOf()

    /**
     * 初始化模拟客户端。
     *
     * 子类应在此完成注册属性钩子、动作钩子或其他测试资源的初始化。
     */
    abstract fun onInit()

    /**
     * 为指定属性注册模拟写入钩子。
     *
     * 属性写入请求会优先交给该钩子处理；同一属性重复注册时会覆盖原有钩子。
     * 如果服务或属性不存在，则忽略本次注册。
     *
     * @param serviceName 服务名称
     * @param propertyName 属性名称
     * @param propertyHook 属性写入时执行的钩子
     */
    @MockFun
    fun registerProperty(
        serviceName: String,
        propertyName: String,
        propertyHook: MockPropertyHook
    ) {
        val siid: Int
        val piid: Int
        specAtt.services
            .firstOrNull { it.name == serviceName }
            ?.also { siid = it.iid }
            ?.properties
            ?.firstOrNull { it.name == propertyName }
            ?.also { piid = it.iid }
            ?: return
        mockProperty[siid to piid] = propertyHook
    }

    /**
     * 为指定动作注册模拟执行钩子。
     *
     * 同一动作重复注册时会覆盖原有钩子；如果服务或动作不存在，则忽略本次注册。
     *
     * @param serviceName 服务名称
     * @param actionName 动作名称
     * @param actionHook 动作执行时调用的钩子
     */
    @MockFun
    fun registerAction(
        serviceName: String,
        actionName: String,
        actionHook: MockActionHook
    ) {
        val siid: Int
        val aiid: Int
        specAtt.services
            .firstOrNull { it.name == serviceName }
            ?.also { siid = it.iid }
            ?.actions
            ?.firstOrNull { it.name == actionName }
            ?.also { aiid = it.iid }
            ?: return
        mockAction[siid to aiid] = actionHook
    }

    /**
     * 根据服务名称和属性名称查找属性规格。
     *
     * @param serviceName 服务名称
     * @param propertyName 属性名称
     * @return 找到的属性规格；服务或属性不存在时返回 `null`
     */
    @MockFun
    fun getProperty(serviceName: String, propertyName: String): SpecProperty? {
        return specAtt.services
            .firstOrNull { it.name == serviceName }
            ?.properties
            ?.firstOrNull { it.name == propertyName }
    }

    /**
     * 根据服务名称和动作名称查找动作规格。
     *
     * @param serviceName 服务名称
     * @param actionName 动作名称
     * @return 找到的动作规格；服务或动作不存在时返回 `null`
     */
    @MockFun
    fun getAction(serviceName: String, actionName: String): SpecAction? {
        return specAtt.services
            .firstOrNull { it.name == serviceName }
            ?.actions
            ?.firstOrNull { it.name == actionName }
    }

    /**
     * 更新模拟存储中的属性值。
     *
     * 传入 `null` 时不执行任何更新。
     *
     * @param att 属性索引，包含服务实例 ID（siid）和属性实例 ID（piid）
     * @param value 要写入的属性值
     */
    @MockManager
    fun <T> update(att: GetAtt, value: T?) {
        value ?: return
        mockStore[att] = value as Any
    }

    /**
     * 将服务名称与属性名称转换为属性索引。
     *
     * 当规格中找不到对应服务或属性时返回 `-1 to -1`。
     *
     * @param propertyName 属性名称
     * @return 由服务实例 ID（siid）和属性实例 ID（piid）组成的属性索引
     */
    @MockFun
    infix fun String.with(propertyName: String): GetAtt {
        val serviceName = this
        val siid: Int
        val piid: Int
        specAtt.services
            .firstOrNull { it.name == serviceName }
            ?.also { siid = it.iid }
            ?.properties
            ?.firstOrNull { it.name == propertyName }
            ?.also { piid = it.iid }
            ?: return -1 to -1
        return siid to piid
    }

    /**
     * 处理属性读取请求，并从模拟存储返回当前值。
     *
     * @param att 要读取的属性索引列表
     * @return 包含模拟属性值的 MIoT 响应；执行异常时返回失败的 [Result]
     */
    override suspend fun onGet(att: Array<out GetAtt>): Result<MiotResponse<PropertyList?>> =
        runCatchingSuspend {
            MiotResponse(
                code = 0,
                message = "",
                result = att.map { info ->
                    Property(
                        did = miotDevice.did,
                        iid = "",
                        siid = info.first,
                        piid = info.second,
                        value = mockStore[info.first to info.second],
                        code = 0,
                        updateTime = null,
                        exeTime = 0
                    )
                }.let { ArrayList(it) }
            )
        }

    /**
     * 处理属性写入请求。
     *
     * 未注册钩子的属性会立即写入模拟存储；已注册钩子的属性会在 [mockScope] 中异步执行，
     * 并取消该属性之前尚未完成的钩子任务。
     *
     * @param att 要写入的属性及其值
     * @return 写入操作结果；执行异常时返回失败的 [Result]
     */
    override suspend fun onSet(att: Array<out SetAtt>): Result<Unit> =
        runCatchingSuspend {
            for (entry in att) {
                val mockFun = mockProperty[entry.siid to entry.piid]
                if (mockFun == null) {
                    mockStore[entry.siid to entry.piid] = entry.value
                    continue
                }
                val property = specAtt.services
                    .firstOrNull { it.iid == entry.siid }
                    ?.properties
                    ?.firstOrNull { it.iid == entry.piid }
                    ?: continue
                val newJob = mockScope.launch {
                    mockFun(property, mockStore, entry.value)
                }.apply {
                    invokeOnCompletion {
                        mockJob.remove(entry.siid to entry.piid)
                    }
                }
                mockJob[entry.siid to entry.piid]?.cancel()
                mockJob[entry.siid to entry.piid] = newJob
            }
        }

    /**
     * 处理动作执行请求，并调用已注册的动作钩子。
     *
     * 未注册钩子或找不到对应动作规格时返回成功但结果为 `null` 的 [Result]。
     *
     * @param siid 服务实例 ID
     * @param aiid 动作实例 ID
     * @param input 动作输入参数
     * @return 动作钩子的返回值；执行异常时返回失败的 [Result]
     */
    override suspend fun onAction(siid: Int, aiid: Int, vararg input: Any): Result<Any?> =
        runCatchingSuspend {
            val action = specAtt.services
                .firstOrNull { it.iid == siid }
                ?.actions
                ?.firstOrNull { it.iid == aiid }
                ?: return@runCatchingSuspend null
            mockAction[siid to aiid]?.invoke(action, mockStore, input)
        }

    @DslMarker
    annotation class MockFun

    @DslMarker
    annotation class MockManager
}