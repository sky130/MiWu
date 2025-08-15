@file:Suppress("UNCHECKED_CAST")

package miwu.support.base

import miwu.annotation.Property
import miwu.annotation.Service
import miwu.support.api.Controller
import miwu.support.icon.Icon
import miwu.icon.Icons
import miwu.icon.NoneIcon
import miwu.support.manager.MiotDeviceManager.ControllerWrapper
import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.translate.TranslateHelper
import miwu.support.unit.Unit
import java.util.concurrent.CopyOnWriteArrayList


abstract class MiwuWidget<T>() {

    val serviceList by lazy {
        val services = this::class.java.annotations.find {
            it is Service
        } as? Service ?: return@lazy arrayListOf()
        return@lazy services.name.toList()
    }
    val propertyList by lazy {
        val properties = this::class.java.annotations.find {
            it is Property
        } as? Property ?: return@lazy arrayListOf()
        return@lazy properties.name.toList()
    }

    open val icon: Icon = NoneIcon
    internal var controllers = CopyOnWriteArrayList<Controller>()
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
    internal var _valueOriginUnit: String = "null"
    internal val _valueList = arrayListOf<Value>()
    internal lateinit var _icons: Icons
    lateinit var translateHelper: TranslateHelper


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
            .filter { it !is ControllerWrapper }
            .forEach { controller ->
                controller.onUpdateValue(siid, piid, value as Any)
            }
    }

    /**
     * 用于用户操作的更新
     */
    fun update(value: T) {
        controllers.find { it is ControllerWrapper }?.onUpdateValue(siid, piid, value as Any)
        controllers
            .filter { it !is ControllerWrapper }
            .forEach { controller ->
                controller.onUpdateValue(siid, piid, value as Any)
            }
    }

    fun action(input: Any? = null) {
        for (controller in controllers) {
            controller.doAction(siid, aiid, input)
        }
    }


    open fun onActionFinish(siid: Int, aiid: Int, value: Any) {}

    open fun init() {}

    /**
     * 用于非用户操作的更新
     */
    @Suppress("UNCHECKED_CAST")
    fun updateValue(value: Any?) {
        try {
            val t = value as T
            onValueChange(t)
        } catch (_: Exception) {
            try {
                val t = value.toString() as T
                onValueChange(t)
            } catch (_: Exception) {

            }
        }
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

}