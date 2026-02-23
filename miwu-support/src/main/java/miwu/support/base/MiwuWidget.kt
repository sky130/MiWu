@file:Suppress("UNCHECKED_CAST")

package miwu.support.base

import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.api.WidgetObserver
import miwu.support.manager.MiotDeviceManager
import miwu.support.translate.TranslateHelper
import miwu.support.unit.ValueUnit

abstract class MiwuWidget<T>() : BaseMiwuWidget<T>() {
    lateinit var translateHelper: TranslateHelper
    internal val iidList = mutableListOf<Pair<Int, Int>>()

    private var _miotDeviceManager: MiotDeviceManager? = null
    private val miotDeviceManager: MiotDeviceManager
        get() = _miotDeviceManager ?: error("MiotDeviceManager not initialized")
    internal val observers = mutableListOf<WidgetObserver<T>>()

    override val siid get() = field.siid
    override val piid get() = field.piid
    override val aiid get() = field.aiid
    override val actionName get() = field.actionName
    override val serviceName get() = field.serviceName
    override val propertyName get() = field.propertyName
    override val description get() = field.desc
    override val serviceDescription get() = field.serviceDescTranslation
    override val defaultValue get() = field.defaultValue!!
    override val valueRange get() = field.valueRange!!
    override val valueStep get() = field.valueStep!!
    override val valueOriginUnit get() = field.valueOriginUnit
    override val valueUnit by lazy { ValueUnit.from(field.valueOriginUnit) }
    override val valueList: List<Value> get() = this.field.valueList
    override val allowWrite get() = field.allowWrite
    override val allowRead get() = field.allowRead
    override val allowNotify get() = field.allowNotify
    override val Icons get() = field.icons
    override val descriptionTranslation by lazy {
        if (field.desc != field.descTranslation) // 如果已经在miot有对应翻译, 则不再重复翻译
            field.descTranslation
        else
            translateHelper.translate(field.desc)
    }
    override val serviceDescriptionTranslation by lazy {
        if (field.serviceDesc != field.serviceDescTranslation) // 如果已经在miot有对应翻译, 则不再重复翻译
            field.serviceDescTranslation
        else
            translateHelper.translate(field.serviceDesc)
    }

    /**
     * 更新 Property
     *
     * @param value 要更新的数据
     *
     * @see [MiotDeviceManager.updateValue]
     */
    fun update(value: T) {
        miotDeviceManager.updateValue(siid, piid, value as Any)
        observers.forEach { it.onUpdateValue(value) }
    }

    /**
     * 指定 siid 和 piid 更新 Property
     *
     * @param siid siid
     * @param piid piid
     * @param value 要更新的数据
     *
     * @see [MiotDeviceManager.updateValue]
     */
    fun update(siid: Int, piid: Int, value: Any) {
        miotDeviceManager.updateValue(siid, piid, value)
        observers.forEach { it.onUpdateValue(siid, piid, value) }
    }

    /**
     * 执行 Action
     *
     * @param input Action 的输入
     *
     * @see [MiotDeviceManager.doAction]
     */
    fun action(vararg input: Any) {
        miotDeviceManager.doAction(siid, aiid, *input)
    }


    /**
     * 执行指定 Action
     *
     * @param siid siid
     * @param aiid aiid
     * @param input Action 的输入
     *
     * @see [MiotDeviceManager.doAction]
     */
    fun action(siid: Int, aiid: Int, vararg input: Any) {
        miotDeviceManager.doAction(siid, aiid, *input)
    }

    internal fun onValueChange(value: T) {
        observers.forEach { it.onUpdateValue(value) }
    }

    internal fun onValueChange(siid: Int, piid: Int, value: Any) {
        observers.forEach { it.onUpdateValue(siid, piid, value) }
    }

    internal fun onActionCallback(siid: Int, aiid: Int, input: Any?) {
        observers.forEach { it.onActionCallback(siid, aiid, input) }
    }

    /**
     * 注册 Property，使得可以监听 Property 的变化
     *
     * @param siid siid
     * @param piid piid
     */
    fun register(siid: Int, piid: Int) {
        iidList.add(siid to piid)
    }

    /**
     * 绑定 [MiotDeviceManager]
     *
     * @param manager [MiotDeviceManager]
     */
    fun bind(manager: MiotDeviceManager) {
        _miotDeviceManager = manager
    }

    /**
     * 添加 Observer
     *
     * @param observer 观察者
     */
    fun addObserver(observer: WidgetObserver<T>) {
        observers.add(observer)
    }

    /**
     * 回收该 Widget
     */
    fun recycler() {
        _miotDeviceManager = null
        observers.clear()
    }
}