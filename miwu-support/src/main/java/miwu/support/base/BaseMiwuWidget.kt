@file:Suppress("UNCHECKED_CAST")

package miwu.support.base

import miwu.annotation.basic.Widget
import miwu.icon.Icons
import miwu.icon.NoneIcon
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.icon.Icon

abstract class BaseMiwuWidget<T>() : Widget {
    internal val field = Field<T>()
    internal val miotSpecAtt get() = field.miotSpecAtt

    open val icon: Icon = NoneIcon
    open val isMultiAttribute = false

    @Suppress("PropertyName")
    abstract val Icons: Icons
    abstract val siid: Int
    abstract val piid: Int
    abstract val aiid: Int
    abstract val actionName: String
    abstract val serviceName: String
    abstract val propertyName: String
    abstract val description: String
    abstract val defaultValue: T
    abstract val valueRange: Pair<T, T>
    abstract val valueStep: T
    abstract val valueOriginUnit: String
    abstract val valueUnit: String
    abstract val valueList: List<Value>
    abstract val allowWrite: Boolean
    abstract val allowRead: Boolean
    abstract val allowNotify: Boolean
    abstract val descriptionTranslation: String

    open fun init() {}

    fun getService(serviceName: String): SpecAtt.Service? {
        if (!field.isSpecAttInitialized) return null
        return miotSpecAtt.services.find { it.type == serviceName }
    }

    fun getProperty(serviceName: String, propertyName: String): SpecAtt.Service.Property? {
        if (!field.isSpecAttInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.properties?.find { it.type == propertyName }
    }

    fun getAction(serviceName: String, actionName: String): SpecAtt.Service.Action? {
        if (!field.isSpecAttInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.actions?.find { it.type == actionName }
    }

    internal data class Field<T>(
        var siid: Int = -1,
        var piid: Int = -1,
        var aiid: Int = -1,
        var desc: String = "",
        var serviceName: String = "",
        var propertyName: String = "",
        var actionName: String = "",
        var descTranslation: String = "",
        var defaultValue: T? = null,
        var valueRange: Pair<T, T>? = null,
        var valueStep: T? = null,
        var valueOriginUnit: String = "",
        val valueList: ArrayList<Value> = arrayListOf(),
        var allowWrite: Boolean = false,
        var allowRead: Boolean = false,
        var allowNotify: Boolean = false,
    ) {
        lateinit var miotSpecAtt: SpecAtt
        lateinit var icons: Icons

        val isSpecAttInitialized get() = ::miotSpecAtt.isInitialized

        internal fun setDefaultValue(value: Any) {
            defaultValue = value as T
        }

        internal fun setValueRange(from: Any, to: Any, step: Any) {
            valueRange = from as T to to as T
            valueStep = step as T
        }
    }
}