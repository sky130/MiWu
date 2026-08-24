package miwu.support

import miwu.annotation.Property
import miwu.miot.model.spec.SpecAction
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.spec.SpecProperty
import miwu.support.api.WidgetObserver
import miwu.support.icon.Icon

abstract class MiwuWrapper<T>(val widget: MiwuWidget<T>) : WidgetObserver<T> {

    private var canUpdate = true
    val propertyListenerList =
        mutableMapOf<Pair<Int, Int>, Pair<SpecProperty, (property: SpecProperty, value: Any) -> Unit>>()
    val actionListenerList =
        mutableMapOf<Pair<Int, Int>, Pair<SpecAction, (action: SpecAction, input: Any) -> Unit>>()

    /**
     * 用于描述设备中的 `property` 或 `action`
     */
    val description get() = widget.description

    /**
     * `property` 或 `action` 描述翻译
     *
     * @see [description]
     */
    val descriptionTranslation get() = widget.descriptionTranslation

    /**
     * 用于描述设备中的 `service`
     */
    val serviceDescription get() = widget.serviceDescription

    /**
     * `service` 描述翻译
     *
     * @see [serviceDescription]
     */
    val serviceDescriptionTranslation get() = widget.serviceDescriptionTranslation

    /**
     * 翻译
     */
    val translateHelper get() = widget.translateHelper

    /**
     * Action 名称
     *
     * @see [SpecAtt.Action]
     */
    val actionName get() = widget.actionName

    /**
     * Service 名称
     *
     * @see [SpecAtt.Service]
     */
    val serviceName get() = widget.serviceName

    /**
     * Property名称
     *
     * @see [SpecAtt.Property]
     */
    val propertyName get() = widget.propertyName

    /**
     * Service Instance ID
     * 服务实例ID
     *
     * 用于区分设备中的 `service`
     *
     * @see [SpecAtt.Service]
     */
    val siid get() = widget.siid

    /**
     * Property Instance ID
     * 属性实例ID
     *
     * 用于区分设备中的 `property`
     *
     * @see [SpecAtt.Property]
     */
    val piid get() = widget.piid

    /**
     * Action Instance ID
     * 动作实例ID
     *
     * 用于区分设备中的 `action`
     *
     * @see [SpecAtt.Action]
     */
    val aiid get() = widget.aiid

    /**
     * 默认图标定义，根据实际情况在对于的 Widget 中设置
     *
     * 如 `light` 设备的亮度条可以设置 icon 为 `brightness`
     *
     * @see Icon
     */
    val icon get() = widget.icon

    /**
     * @see [SpecAtt.Property.valueList]
     */
    val valueList get() = widget.valueList
    val valueRange get() = widget.valueRange

    /**
     * 步长
     *
     * 该属性只在以下情况存在
     *
     * - 在 `property` 有 `value-range` 的情况
     *
     * @see [SpecAtt.Property.valueRange]
     */
    val valueStep get() = widget.valueStep

    /**
     * `value` 的原始计量单位，如 `celsius`
     *
     * 如果需要获取转换过的计量单位，请使用 [valueUnit]
     *
     * @see [SpecAtt.Property.unit]
     */
    val valueOriginUnit get() = widget.valueOriginUnit

    /**
     * `value` 的计量单位，可用于直接展示，如 `°C`
     *
     * 如果需要获取未转换过的计量单位，请使用 [valueOriginUnit]
     *
     * @see [miwu.support.unit.ValueUnit]
     */
    val valueUnit get() = widget.valueUnit

    /**
     * MiotDevice 设备类型
     *
     * 如 `light`, `fan`, `switch`
     */
    val deviceType get() = widget.deviceType

    /**
     * 默认值
     *
     * 该属性只在以下情况存在
     *
     * - 在 `property` 有 `value-list` 的情况
     *
     * @see [SpecAtt.Property.Value]
     */
    val defaultValue get() = widget.defaultValue

    val Pair<T, T>.from get() = first
    val Pair<T, T>.to get() = second

    init {
        widget.addObserver(this)
    }

    /**
     * 初始化 Wrapper
     */
    abstract fun initWrapper()

    abstract override fun onUpdateValue(value: T)
    open fun init() = initWrapper()
    open fun onActionCallback(value: Any) = Unit

    /**
     * 停止 Value 的更新
     */
    @WrapperManager
    fun stopUpdate() {
        canUpdate = false
    }

    /**
     * 恢复 Value 的更新
     */
    @WrapperManager
    fun continueUpdate() {
        canUpdate = true
    }

    override fun onActionCallback(siid: Int, aiid: Int, output: Any?) {
        // Action 的回调是一次性的，不应该拦截
        if (widget.isMultiAttribute)
            actionListenerList[siid to aiid]?.also { (action, func) ->
                func(action, output ?: Unit)
            }
        if (this.siid to this.aiid == siid to aiid) {
            onActionCallback(output ?: Unit)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onUpdateValue(
        siid: Int, piid: Int, value: Any
    ) {
        if (!canUpdate) return
        if (widget.isMultiAttribute)
            propertyListenerList[siid to piid]?.also { (property, func) ->
                func(property, value)
            }
        if (this.siid to this.piid == siid to piid) {
            onUpdateValue(value as T)
        }
    }

    /**
     * 更新 Property
     *
     * @param value 要更新的数据
     */
    @WrapperManager
    fun update(value: T) = widget.update(value)

    @Suppress("UNCHECKED_CAST")
    @WrapperManager
    fun updateValue(value: SpecAtt.Property.Value) = update(value.value as T)

    /**
     * 指定 siid 和 piid 更新 Property
     *
     * @param siid siid
     * @param piid piid
     * @param value 要更新的数据
     */
    @WrapperManager
    fun update(siid: Int, piid: Int, value: Any) = widget.update(siid, piid, value)

    /**
     * 执行 Action
     *
     * @param input Action 的输入
     */
    @WrapperManager
    fun action(vararg input: Any) = widget.action(*input)

    /**
     * 执行指定 Action
     *
     * @param siid siid
     * @param aiid aiid
     * @param input Action 的输入
     */
    @WrapperManager
    fun action(siid: Int, aiid: Int, vararg input: Any) = widget.action(siid, aiid, *input)

    /**
     * 根据 `ServiceName` 获取对应的 [SpecAtt.Service]
     *
     * 如果不存在该 [SpecAtt.Service] 则返回 null
     *
     * @see [miwu.spec.MiotSpec.Service]
     */
    @WrapperFun
    fun getService(serviceName: String): SpecAtt.Service? = widget.getService(serviceName)

    /**
     * 根据 `ServiceName` 和 `ActionName` 获取对应的 [SpecAtt.Action]
     *
     * 如果不存在该 [SpecAtt.Action] 则返回 null
     *
     * @see [miwu.spec.MiotSpec.Service]
     */
    @WrapperFun
    fun getAction(serviceName: String, actionName: String): SpecAtt.Action? =
        widget.getAction(serviceName, actionName)

    /**
     * 根据 `ServiceName` 和 `PropertyName` 获取对应的 [SpecAtt.Property]
     *
     * 如果不存在该 [SpecAtt.Property] 则返回 null
     *
     * @see [miwu.spec.MiotSpec.Service]
     * @see [miwu.spec.MiotSpec.Property]
     */
    @WrapperFun
    fun getProperty(serviceName: String, propertyName: String): SpecProperty? =
        widget.getProperty(serviceName, propertyName)

    /**
     * 注册 Property，使得可以监听 Property 的变化
     *
     * @param siid siid
     * @param piid piid
     * @param block 回调函数
     */
    @WrapperFun
    fun registerProperty(
        serviceName: String,
        propertyName: String,
        block: (property: SpecProperty, value: Any) -> Unit
    ) {
        val siid = getService(serviceName)?.iid ?: return
        val property = getProperty(serviceName, propertyName) ?: return
        val piid = property.iid
        registerProperty(siid, piid)
        propertyListenerList[siid to piid] = property to block
    }

    /**
     * 注册 Action，使得可以获取 Action 的回调
     *
     * @param siid siid
     * @param aiid piid
     * @param block 回调函数
     */
    @WrapperFun
    fun registerAction(
        serviceName: String,
        actionName: String,
        block: (action: SpecAction, input: Any) -> Unit
    ) {
        val siid = getService(serviceName)?.iid ?: return
        val action = getAction(serviceName, actionName) ?: return
        val aiid = action.iid
        registerAction(siid, aiid)
        actionListenerList[siid to aiid] = action to block
    }

    @WrapperFun
    fun registerAction(siid: Int, aiid: Int) {
        if (widget.isMultiAttribute) widget.registerAction(siid, aiid)
    }

    @WrapperFun
    fun registerProperty(siid: Int, piid: Int) {
        if (widget.isMultiAttribute) widget.registerProperty(siid, piid)
    }

    // 搜索 `valueList` 中的 `value`
    @WrapperFun
    fun valueListOf(desc: String): SpecAtt.Property.Value =
        widget.valueList.first { it.description == desc }

    @WrapperFun
    fun valueListOfOrNull(desc: String): SpecAtt.Property.Value? =
        widget.valueList.firstOrNull { it.description == desc }

    @WrapperFun
    fun valueListOf(value: Int): SpecAtt.Property.Value =
        widget.valueList.first { it.value == value }

    @WrapperFun
    fun valueListOfOrNull(value: Int): SpecAtt.Property.Value? =
        widget.valueList.firstOrNull { it.value == value }

    @WrapperFun
    fun SpecAtt.Property.valueListOf(desc: String): SpecAtt.Property.Value =
        this.valueList!!.first { it.description == desc }

    @WrapperFun
    fun SpecAtt.Property.valueListOfOrNull(desc: String): SpecAtt.Property.Value? =
        this.valueList?.firstOrNull { it.description == desc }

    @WrapperFun
    fun SpecAtt.Property.valueListOf(value: Int): SpecAtt.Property.Value =
        this.valueList!!.first { it.value == value }

    @WrapperFun
    fun SpecAtt.Property.valueListOfOrNull(value: Int): SpecAtt.Property.Value? =
        this.valueList?.firstOrNull { it.value == value }

    /**
     * 用于注解 Wrapper 内部封装的辅助函数
     */
    @DslMarker
    annotation class WrapperFun

    /**
     * 用于注解 Wrapper 和 MiotManager 有关的交互的函数
     *
     * 包括 [canUpdate]
     */
    @DslMarker
    annotation class WrapperManager
}
