@file:Suppress("UNCHECKED_CAST")
package miwu.support.base

import miwu.annotation.basic.Widget
import miwu.icon.Icons
import miwu.icon.NoneIcon
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.api.Controller
import miwu.support.icon.Icon
import miwu.support.manager.MiotDeviceManager.MiotDeviceManagerController
import miwu.support.translate.TranslateHelper
import miwu.support.unit.Unit

abstract class MiwuWidget<T>() : Widget {
    open val icon: Icon = NoneIcon
    open val isMultiAttribute = false
    internal var controllers = ArrayList<Controller>()
    internal var _siid = -1
    internal var _piid = -1
    internal var _aiid = -1
    internal var _desc = ""
    internal var _serviceName = ""
    internal var _propertyName = ""
    internal var _actionName = ""
    internal var _descTranslation = ""
    internal var _defaultValue: T? = null
    internal var _valueRange: Pair<T, T>? = null
    internal var _valueStep: T? = null
    internal var _valueOriginUnit: String = ""
    internal val _valueList = arrayListOf<Value>()
    internal var _allowWrite: Boolean = false
    internal var _allowRead: Boolean = false
    internal var _allowNotify: Boolean = false

    internal lateinit var _icons: Icons
    internal lateinit var miotSpecAtt: SpecAtt
    lateinit var translateHelper: TranslateHelper
    internal val iidList = mutableListOf<Pair<Int, Int>>()

    val siid get() = _siid
    val piid get() = _piid
    val aiid get() = _aiid
    val actionName get() = _actionName
    val serviceName get() = _serviceName
    val propertyName get() = _propertyName
    val description get() = _desc
    val defaultValue get() = _defaultValue!!
    val valueRange get() = _valueRange!!
    val valueStep get() = _valueStep!!
    val valueOriginUnit get() = _valueOriginUnit
    val valueUnit by lazy { Unit.from(_valueOriginUnit) }
    val valueList: List<Value> get() = _valueList
    val allowWrite get() = _allowWrite
    val allowRead get() = _allowRead
    val allowNotify get() = _allowNotify
    val Icons get() = _icons
    val descriptionTranslation by lazy {
        if (_desc != _descTranslation) // 如果已经在miot有对应翻译, 则不再重复翻译
            _descTranslation
        else
            translateHelper.translate(_desc)
    }

    internal fun setDefaultValue(value: Any) {
        _defaultValue = value as T
    }

    internal fun setValueRange(from: Any, to: Any, step: Any) {
        _valueRange = from as T to to as T
        _valueStep = step as T
    }

    internal fun onValueChange(value: T) {
        controllers
            .filter { it !is MiotDeviceManagerController }
            .forEach { controller ->
                controller.onUpdateValue(siid, piid, value as Any)
            }
    }

    internal fun onValueChange(siid: Int, piid: Int, value: T) {
        controllers
            .filter { it !is MiotDeviceManagerController }
            .forEach { controller ->
                controller.onUpdateValue(siid, piid, value as Any)
            }
    }

    /**
     * 用于用户操作的更新
     */
    fun update(value: T) {
        controllers.find { it is MiotDeviceManagerController }
            ?.onUpdateValue(siid, piid, value as Any)
        controllers
            .filter { it !is MiotDeviceManagerController }
            .forEach { controller ->
                controller.onUpdateValue(siid, piid, value as Any)
            }
    }

    fun action(vararg input: Any) {
        for (controller in controllers) {
            controller.doAction(siid, aiid, *input)
        }
    }

    fun action(siid: Int, aiid: Int, vararg input: Any) {
        for (controller in controllers) {
            controller.doAction(siid, aiid, *input)
        }
    }

    fun onActionCallback(siid: Int, aiid: Int, output: Any) {
        for (controller in controllers) {
            controller.onActionCallback(siid, aiid, output)
        }
    }

    open fun init() {}

    /**
     * 用于非用户操作的更新
     */
    @Suppress("UNCHECKED_CAST")
    fun updateValue(value: Any?) {
        runCatching {
            val t = value as T
            onValueChange(t)
        }.onFailure {
            runCatching {
                val t = value.toString() as T
                onValueChange(t)
            }
        }
    }

    /**
     * 用于非用户操作的更新
     */
    @Suppress("UNCHECKED_CAST")
    fun updateValue(siid: Int, piid: Int, value: Any?) {
        if (siid to piid !in iidList) return
        runCatching {
            val t = value as T
            onValueChange(siid, piid, t)
        }.onFailure {
            runCatching {
                val t = value.toString() as T
                onValueChange(siid, piid, t)
            }
        }
    }

    fun register(siid: Int, piid: Int) {
        iidList.add(siid to piid)
    }

    fun bind(controller: Controller) {
        controllers.add(controller)
    }

    fun unbind(controller: Controller? = null) {
        if (controller == null) {
            controllers.clear()
        } else {
            controllers.remove(controller)
        }
    }

    fun getService(serviceName: String): SpecAtt.Service? {
        if (!::miotSpecAtt.isInitialized) return null
        return miotSpecAtt.services.find { it.type == serviceName }
    }

    fun getProperty(serviceName: String, propertyName: String): SpecAtt.Service.Property? {
        if (!::miotSpecAtt.isInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.properties?.find { it.type == propertyName }
    }

    fun getAction(serviceName: String, actionName: String): SpecAtt.Service.Action? {
        if (!::miotSpecAtt.isInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.actions?.find { it.type == actionName }
    }

}