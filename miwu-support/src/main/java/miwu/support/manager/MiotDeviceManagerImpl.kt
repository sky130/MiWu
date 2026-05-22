@file:Suppress("UNCHECKED_CAST")

package miwu.support.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.annotation.Widgets
import miwu.icon.Icons
import miwu.support.api.Cache
import miwu.support.MiwuWidget
import miwu.support.layout.MiwuWidgetLayout
import miwu.support.generated.device.DeviceRegistry
import miwu.miot.att.get.GetAtt
import miwu.miot.client.MiotDeviceClient
import miwu.support.urn.Urn
import miwu.support.translate.TranslateHelper
import miwu.miot.model.att.PropertyList
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.mock.MockMiotDeviceClientBuilder
import miwu.support.mock.DefaultMockMiotDeviceClient
import miwu.support.mock.MockMiotDeviceClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

typealias MiwuWidgetClass = Class<MiwuWidget<*>>

/**
 * Miot 设备管理器实现
 *
 * 负责设备的生命周期管理、属性轮询更新、值更新和动作执行。
 * Widget 的加载和配置委托给 [WidgetLoader] 处理。
 *
 * @param miot 用于调用 MiotDeviceClient 接口，为 null 时使用 mockMiotDeviceClient
 * @param specAttrProvider 用于调用 MiotManager 接口获取设备属性规格
 * @param device 设备详情
 * @param icons 图标资源
 * @param cache 用于缓存设备属性
 * @param translateHelper 翻译帮助器
 * @param dispatcher 用于更新 UI 数据的线程调度器
 * @param callback 用于回调设备初始化状态
 * @param mockMiotDeviceClient 用于模拟设备的构建器
 */
class MiotDeviceManagerImpl internal constructor(
    miot: MiotDeviceClient?,
    val specAttrProvider: MiotSpecAttrProvider,
    val device: MiotDevice,
    val icons: Icons,
    val cache: Cache,
    val translateHelper: TranslateHelper,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val callback: Callback? = null,
    val mockMiotDeviceClient: MockMiotDeviceClientBuilder = ::DefaultMockMiotDeviceClient
) : MiotDeviceManager() {

    companion object {
        private const val STALE_PROTECTION_BUFFER = 30000L
        private const val REFRESH_INTERVAL = 1000L
    }

    private val widgetHolders = CopyOnWriteArrayList<WidgetLoader.WidgetHolder>()
    private val supportWidget = mutableSetOf<MiwuWidgetClass>()
    private val deviceSpecType = device.specType ?: ""
    private val deviceUrn = Urn.parseFrom(deviceSpecType)
    private val dirtyProperties = ConcurrentHashMap<Pair<Int, Int>, Long>()
    private val job = Job()
    private val scope = CoroutineScope(job)
    private val miot: MiotDeviceClient by lazy {
        miot ?: mockMiotDeviceClient(
            deviceUrn.name,
            scope,
            specAtt,
            device
        ).apply {
            if (this is MockMiotDeviceClient) onInit()
        }
    }
    private lateinit var specAtt: SpecAtt

    override val layout = MiwuWidgetLayout()


    /**
     * 初始化设备管理器
     *
     * 在协程中执行以下操作：
     * 1. 初始化设备（加载支持的 widget 类型）
     * 2. 初始化 widgets（从 SpecAtt 加载并配置）
     * 3. 回调通知设备初始化完成
     * 4. 启动属性轮询
     */
    override fun init() {
        scope.launch {
            initDevice()
            initWidgets()
            withContext(dispatcher) { callback?.onDeviceInitiated() }
            run()
        }
    }

    /**
     * 停止设备管理器
     *
     * 取消协程任务，清空布局，回收所有 widget 资源。
     */
    override fun stop() {
        job.cancel()
        layout.clear()
        widgetHolders.forEach { it.recycler() }
        widgetHolders.clear()
    }

    /**
     * 初始化设备
     *
     * 从 [DeviceRegistry] 查找设备类，读取 [Widgets] 注解，填充 [supportWidget] 集合。
     */
    private fun initDevice() {
        val device = DeviceRegistry.registry[deviceUrn.name]?.java ?: return
        val widgetAnnotations =
            device.annotations.firstOrNull { it is Widgets } as? Widgets ?: return
        supportWidget += (widgetAnnotations.widgets as Array<KClass<MiwuWidget<*>>>).map { it.java }
    }

    /**
     * 启动属性轮询
     *
     * 在协程中循环调用 [forEach] 获取最新属性值，间隔为 [REFRESH_INTERVAL] 毫秒。
     */
    private fun run() {
        scope.launch {
            while (true) {
                forEach()
                delay(REFRESH_INTERVAL)
            }
        }
    }

    /**
     * 初始化 widgets
     *
     * 获取设备属性规格，初始化变量和语言映射，然后委托 [WidgetLoader] 加载 widgets。
     */
    private suspend fun initWidgets() {
        val att = getAtt() ?: return

        specAtt = att
        callback?.onDeviceAttLoaded(att)
        cache.putSpecAtt(deviceSpecType, att)

        att.initVariable()
        getLanguageMap()?.let { att.convertLanguage(it) }

        val widgetLoader = WidgetLoader(device, this, supportWidget, layout, icons, translateHelper)
        widgetHolders.addAll(widgetLoader.loadWidgets(att))
    }

    /**
     * 更新指定属性的值
     *
     * 标记属性为脏，更新所有匹配的 widget，然后调用 MiotDeviceClient 设置值。
     * 设置成功后保持脏标记 30 秒，防止飞行中的轮询返回旧值覆盖本地更新。
     *
     * @param siid 服务 ID
     * @param piid 属性 ID
     * @param value 新的属性值
     */
    override fun updateValue(siid: Int, piid: Int, value: Any) {
        scope.launch {
            val key = siid to piid
            dirtyProperties[key] = System.currentTimeMillis()

            for (i in widgetHolders) {
                val widget = i.widget
                if (widget.allowRead && widget.siid == siid && widget.piid == piid) widget.updateValue(
                    value
                )
                if (widget.isMultiAttribute) widget.updateValue(siid, piid, value)
            }

            val result = miot.set(device, arrayOf(siid to piid to value))
            if (result.isSuccess) {
                scope.launch {
                    delay(STALE_PROTECTION_BUFFER)
                    dirtyProperties.remove(key)
                }
            }
        }
    }

    /**
     * 执行设备动作
     *
     * 调用 MiotDeviceClient 执行动作，然后通知所有匹配的 widget 回调。
     *
     * @param siid 服务 ID
     * @param aiid 动作 ID
     * @param input 动作输入参数
     */
    override fun doAction(siid: Int, aiid: Int, vararg input: Any) {
        scope.launch {
            val action = siid to aiid
            val result = miot.action(device, siid, aiid, *input).getOrNull() ?: return@launch
            for (holder in widgetHolders) {
                with(holder.widget) {
                    if (this.siid to this.aiid == action ||
                        isMultiAttribute && siid to aiid in aiidList
                    ) {
                        onActionCallback(
                            siid,
                            aiid,
                            result
                        )
                    }
                }
            }
        }
    }

    /**
     * 获取设备属性规格
     *
     * 优先从缓存获取，缓存未命中则从网络请求。
     *
     * @return 设备属性规格，获取失败返回 null
     */
    private suspend fun getAtt() =
        cache.getSpecAtt(deviceSpecType)
            ?: device.getSpecAtt(specAttrProvider).getOrNull()

    /**
     * 获取语言映射表
     *
     * 优先从缓存获取，缓存未命中则从网络请求。
     *
     * @return 语言映射表，获取失败返回 null
     */
    private suspend fun getLanguageMap() =
        cache.getLanguageMap(deviceSpecType)
            ?: device.getSpecAttLanguageMap(specAttrProvider).getOrNull()

    /**
     * 轮询获取所有 widget 的最新属性值
     *
     * 收集所有需要读取的属性，调用 MiotDeviceClient 批量获取，然后更新 widget。
     */
    private suspend fun forEach() = withContext(Dispatchers.IO) {
        val attList = mutableSetOf<GetAtt>()
        for (holder in widgetHolders) {
            val widget = holder.widget
            if (widget.isMultiAttribute) attList.addAll(widget.piidList)
            if (!widget.allowRead || widget.piid == -1) continue
            attList.add(widget.siid to widget.piid)
        }
        if (attList.isEmpty()) return@withContext
        miot.get(device, attList.toTypedArray()).onSuccess {
            update(it.result ?: return@onSuccess)
        }
    }

    /**
     * 更新 widget 的属性值
     *
     * 遍历所有 widget，将获取到的属性值更新到对应的 widget。
     * 跳过脏属性（本地已更新但云端未确认的属性），避免覆盖用户刚设置的值。
     *
     * @param list 获取到的属性值列表
     */
    private suspend fun update(list: PropertyList) = withContext(dispatcher) {
        val now = System.currentTimeMillis()

        val iterator = dirtyProperties.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > STALE_PROTECTION_BUFFER * 2) {
                iterator.remove()
            }
        }

        for (holder in widgetHolders) {
            val widget = holder.widget
            for (att in list) {
                val attKey = att.siid to att.piid

                // 跳过脏属性, 本地值优先于可能过时的云端值
                if (dirtyProperties.containsKey(attKey)) continue

                if (attKey == widget.siid to widget.piid) {
                    widget.updateValue(att.value)
                }
                if (attKey in widget.piidList) {
                    widget.updateValue(att.siid, att.piid, att.value)
                }
            }
        }
    }


    /**
     * 用于更新 MiwuWidget 的属性值
     *
     * MiwuWidget 会绑定一个 Property, 默认情况下通过该方法更新对应的属性值,
     * 如果需要绑定多个 Property, 请使用 [updateValue(siid: Int, piid: Int, value: Any?)] 方法
     *
     * @param value 属性值
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> MiwuWidget<T>.updateValue(value: Any?) {
        runCatching {
            val t = value as T
            onValueChange(t)
        }.recoverCatching {
            val t = value.toString() as T
            onValueChange(t)
        }
    }

    /**
     * 用于更新 MiwuWidget 的属性值
     *
     * 可以指定 siid 和 piid 来更新对应 Property 的属性
     *
     * @param siid siid
     * @param piid piid
     * @param value 属性值
     */
    private fun MiwuWidget<*>.updateValue(siid: Int, piid: Int, value: Any?) {
        if (siid to piid !in piidList) return
        runCatching {
            onValueChange(siid, piid, value!!)
        }.recoverCatching {
            onValueChange(siid, piid, value.toString() as Any)
        }
    }
}

