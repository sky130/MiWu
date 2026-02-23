@file:Suppress("UNCHECKED_CAST")

package miwu.support.base

import miwu.annotation.basic.Widget
import miwu.icon.Icons
import miwu.icon.NoneIcon
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecAtt.Service.Property.Value
import miwu.support.icon.Icon

/**
 * BaseMiwuWidget
 *
 */
abstract class BaseMiwuWidget<T>() : Widget {
    internal val field = Field<T>()
    internal val miotSpecAtt get() = field.miotSpecAtt

    /**
     * 默认图标定义，根据实际情况在对于的 Widget 中设置
     *
     * 如 `light` 设备的亮度条可以设置 icon 为 `brightness`
     *
     * @see Icon
     */
    open val icon: Icon = NoneIcon

    /**
     * 是否支持多属性
     *
     * 用于标记当前 Widget 是否支持多属性，
     * 如果需要在一个 Widget 里面创建子 Widget 或者是监听其他 Property 则需要设置为 true
     */
    open val isMultiAttribute = false

    /**
     * 图标库
     * 由 [miwu.support.manager.MiotDeviceManager] 管理
     */
    @Suppress("PropertyName")
    abstract val Icons: Icons

    /**
     * Service Instance ID
     * 服务实例ID
     *
     * 用于区分设备中的 `service`
     *
     * @see [SpecAtt.Service]
     */
    abstract val siid: Int

    /**
     * Property Instance ID
     * 属性实例ID
     *
     * 用于区分设备中的 `property`
     *
     * @see [SpecAtt.Service.Property]
     */
    abstract val piid: Int

    /**
     * Action Instance ID
     * 动作实例ID
     *
     * 用于区分设备中的 `action`
     *
     * @see [SpecAtt.Service.Action]
     */
    abstract val aiid: Int

    /**
     * Service 名称
     *
     * @see [SpecAtt.Service]
     */
    abstract val serviceName: String

    /**
     * Property名称
     *
     * @see [SpecAtt.Service.Property]
     */
    abstract val propertyName: String

    /**
     * Action 名称
     *
     * @see [SpecAtt.Service.Action]
     */
    abstract val actionName: String

    /**
     * 用于描述设备中的 `property` 或 `action`
     */
    abstract val description: String

    /**
     * 用于描述设备中的 `service`
     */
    abstract val serviceDescription: String

    /**
     * 默认值
     *
     * 该属性只在以下情况存在
     *
     * - 在 `property` 有 `value-list` 的情况
     *
     * @see [SpecAtt.Service.Property.Value]
     */
    abstract val defaultValue: T

    /**
     * 范围
     *
     * 该属性只在以下情况存在
     *
     * - 在 `property` 有 `value-range` 的情况
     *
     * @see [SpecAtt.Service.Property.valueRange]
     */
    abstract val valueRange: Pair<T, T>

    /**
     * 步长
     *
     * 该属性只在以下情况存在
     *
     * - 在 `property` 有 `value-range` 的情况
     *
     * @see [SpecAtt.Service.Property.valueRange]
     */
    abstract val valueStep: T

    /**
     * `value` 的计量单位，可用于直接展示，如 `°C`
     *
     * 如果需要获取未转换过的计量单位，请使用 [valueOriginUnit]
     *
     * @see [miwu.support.unit.ValueUnit]
     */
    abstract val valueUnit: String

    /**
     * `value` 的原始计量单位，如 `celsius`
     *
     * 如果需要获取转换过的计量单位，请使用 [valueUnit]
     *
     * @see [SpecAtt.Service.Property.unit]
     */
    abstract val valueOriginUnit: String

    /**
     * @see [SpecAtt.Service.Property.valueList]
     */
    abstract val valueList: List<Value>

    /**
     * `property` 描述
     *
     * 是否可以写入
     *
     * @see [SpecAtt.Service.Property.access]
     */
    abstract val allowWrite: Boolean

    /**
     * `property` 描述
     *
     * 是否可以读取
     *
     * @see [SpecAtt.Service.Property.access]
     */
    abstract val allowRead: Boolean

    /**
     * `property` 描述
     *
     * 是否可以通知
     *
     * @see [SpecAtt.Service.Property.access]
     */
    abstract val allowNotify: Boolean

    /**
     * `property` 或 `action` 描述翻译
     *
     * @see [description]
     */
    abstract val descriptionTranslation: String

    /**
     * `service` 描述翻译
     *
     * @see [serviceDescription]
     */
    abstract val serviceDescriptionTranslation: String

    /**
     * 初始化 Widget
     */
    open fun init() {}

    /**
     * 根据 `ServiceName` 获取对应的 [SpecAtt.Service]
     *
     * 如果不存在该 [SpecAtt.Service] 则返回 null
     *
     * @see [miwu.spec.Service]
     */
    fun getService(serviceName: String): SpecAtt.Service? {
        if (!field.isSpecAttInitialized) return null
        return miotSpecAtt.services.find { it.type == serviceName }
    }

    /**
     * 根据 `ServiceName` 和 `PropertyName` 获取对应的 [SpecAtt.Service.Property]
     *
     * 如果不存在该 [SpecAtt.Service.Property] 则返回 null
     *
     * @see [miwu.spec.Service]
     * @see [miwu.spec.Property]
     */
    fun getProperty(serviceName: String, propertyName: String): SpecAtt.Service.Property? {
        if (!field.isSpecAttInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.properties?.find { it.type == propertyName }
    }

    /**
     * 根据 `ServiceName` 和 `ActionName` 获取对应的 [SpecAtt.Service.Action]
     *
     * 如果不存在该 [SpecAtt.Service.Action] 则返回 null
     *
     * @see [miwu.spec.Service]
     */
    fun getAction(serviceName: String, actionName: String): SpecAtt.Service.Action? {
        if (!field.isSpecAttInitialized) return null
        val service = miotSpecAtt.services.find { it.type == serviceName } ?: return null
        return service.actions?.find { it.type == actionName }
    }

    // 一个内部类，用于管理 Widget 的字段
    internal data class Field<T>(
        var siid: Int = -1,
        var piid: Int = -1,
        var aiid: Int = -1,
        var desc: String = "",
        var serviceName: String = "",
        var propertyName: String = "",
        var actionName: String = "",
        var descTranslation: String = "",
        var serviceDesc: String = "",
        var serviceDescTranslation: String = "",
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