@file:Suppress("UNCHECKED_CAST")

package miwu.support.base

import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.api.Observer
import miwu.support.manager.MiotDeviceManager
import miwu.support.translate.TranslateHelper
import miwu.support.unit.Unit

abstract class MiwuWidget<T>() : BaseMiwuWidget<T>() {
    lateinit var translateHelper: TranslateHelper
    internal val iidList = mutableListOf<Pair<Int, Int>>()

    private var _miotDeviceManager: MiotDeviceManager? = null
    private val miotDeviceManager: MiotDeviceManager
        get() = _miotDeviceManager ?: error("MiotDeviceManager not initialized")
    internal val observers = mutableListOf<Observer<T>>()

    override val siid get() = field.siid
    override val piid get() = field.piid
    override val aiid get() = field.aiid
    override val actionName get() = field.actionName
    override val serviceName get() = field.serviceName
    override val propertyName get() = field.propertyName
    override val description get() = field.desc
    override val defaultValue get() = field.defaultValue!!
    override val valueRange get() = field.valueRange!!
    override val valueStep get() = field.valueStep!!
    override val valueOriginUnit get() = field.valueOriginUnit
    override val valueUnit by lazy { Unit.from(field.valueOriginUnit) }
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

    fun update(value: T) {
        miotDeviceManager.updateValue(siid, piid, value as Any)
        observers.forEach { it.onUpdateValue(value) }
    }

    fun update(siid: Int, piid: Int, value: Any) {
        miotDeviceManager.updateValue(siid, piid, value)
        observers.forEach { it.onUpdateValue(siid, piid, value) }
    }

    fun action(vararg input: Any) {
        miotDeviceManager.doAction(siid, aiid, *input)
    }

    fun action(siid: Int, aiid: Int, vararg input: Any) {
        miotDeviceManager.doAction(siid, aiid, *input)
    }

    fun onValueChange(value: T) {
        observers.forEach { it.onUpdateValue(value) }
    }

    fun onValueChange(siid: Int, piid: Int, value: Any) {
        observers.forEach { it.onUpdateValue(siid, piid, value) }
    }

    fun onActionCallback(siid: Int, aiid: Int, input: Any?) {
        observers.forEach { it.onActionCallback(siid, aiid, input) }
    }

    fun register(siid: Int, piid: Int) {
        iidList.add(siid to piid)
    }

    fun bind(manager: MiotDeviceManager) {
        _miotDeviceManager = manager
    }

    fun bind(observer: Observer<T>) {
        observers.add(observer)
    }

    fun recycler() {
        _miotDeviceManager = null
        observers.clear()
    }
}