@file:Suppress("UNCHECKED_CAST")

package miwu.support.manager

import miwu.annotation.ValueList
import miwu.annotation.widget.Body
import miwu.annotation.widget.Footer
import miwu.annotation.widget.Header
import miwu.annotation.widget.SubFooter
import miwu.annotation.widget.SubHeader
import miwu.support.MiwuWidget
import miwu.support.generated.widget.ActionRegistry
import miwu.support.generated.widget.PropertyRegistry
import miwu.support.layout.MiwuWidgetLayout
import miwu.support.urn.Urn
import miwu.miot.model.spec.SpecAtt
import miwu.icon.Icons
import miwu.miot.model.miot.MiotDevice
import miwu.support.translate.TranslateHelper
import kotlin.reflect.KClass

/**
 * Widget 加载器，负责从 [SpecAtt] 中发现、创建、配置 widgets 并分配到布局
 *
 * 职责：
 * - 遍历设备的 services/properties/actions 发现可用的 widgets
 * - 创建 widget 实例并配置其 field
 * - 根据注解将 widget 分配到 [MiwuWidgetLayout] 的对应区域
 *
 * @param manager 设备管理器实例，用于绑定 widget
 * @param supportWidget 支持的 widget 类型集合
 * @param layout widget 布局容器
 * @param icons 图标资源
 * @param translateHelper 翻译帮助器
 */
class WidgetLoader(
    private val device: MiotDevice,
    private val manager: MiotDeviceManager,
    private val supportWidget: Set<MiwuWidgetClass>,
    private val layout: MiwuWidgetLayout,
    private val icons: Icons,
    private val translateHelper: TranslateHelper,
) {

    private val deviceType = Urn.parseFrom(device.specType!!).name

    /**
     * widget 持有者，用于绑定和回收 widget
     *
     * @param widget 被持有的 widget 实例
     */
    inner class WidgetHolder(val widget: MiwuWidget<*>) {
        /**
         * 绑定 widget 到设备管理器
         */
        fun bind() {
            widget.bind(manager)
        }

        /**
         * 回收 widget，清理资源
         */
        fun recycler() {
            widget.recycler()
        }
    }

    /**
     * 从 SpecAtt 加载所有 widgets
     *
     * 遍历所有 services，处理其 properties 和 actions，创建对应的 widget 实例。
     * 对于带有多属性（[MiwuWidget.isMultiAttribute]）的 widget，会设置 [SpecAtt] 引用。
     *
     * @param specAtt 设备属性规格
     * @return 创建的 widget 持有者列表
     */
    fun loadWidgets(specAtt: SpecAtt): List<WidgetHolder> {
        val holders = mutableListOf<WidgetHolder>()

        for (service in specAtt.services) {
            holders += loadWidgetsForService(service, specAtt)
        }

        return holders
    }

    /**
     * 处理单个 service 的 widgets
     *
     * 分别处理 service 下的 properties 和 actions。
     *
     * @param service 服务规格
     * @param specAtt 设备属性规格（用于多属性 widget）
     * @return 该 service 下创建的 widget 持有者列表
     */
    private fun loadWidgetsForService(
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ): List<WidgetHolder> {
        val holders = mutableListOf<WidgetHolder>()

        service.properties?.let { holders += loadProperties(service, it, specAtt) }
        service.actions?.let { holders += loadActions(service, it, specAtt) }

        return holders
    }

    /**
     * 处理 service 下的所有 properties
     *
     * 查找每个 property 对应的 widget class，创建实例并配置。
     *
     * @param service 服务规格
     * @param properties 属性列表
     * @param specAtt 设备属性规格
     * @return 创建的 widget 持有者列表
     */
    private fun loadProperties(
        service: SpecAtt.Service,
        properties: List<SpecAtt.Property>,
        specAtt: SpecAtt
    ): List<WidgetHolder> {
        val holders = mutableListOf<WidgetHolder>()

        for (property in properties) {
            val widgetClass = resolvePropertyWidget(service.type, property.type) ?: continue
            holders += createPropertyWidget(widgetClass, property, service, specAtt)
        }

        return holders
    }

    /**
     * 处理 service 下的所有 actions
     *
     * 查找每个 action 对应的 widget class，创建实例并配置。
     *
     * @param service 服务规格
     * @param actions 动作列表
     * @param specAtt 设备属性规格
     * @return 创建的 widget 持有者列表
     */
    private fun loadActions(
        service: SpecAtt.Service,
        actions: List<SpecAtt.Action>,
        specAtt: SpecAtt
    ): List<WidgetHolder> {
        val holders = mutableListOf<WidgetHolder>()

        for (action in actions) {
            val widgetClass = resolveActionWidget(service.type, action.type) ?: continue
            holders += createAndConfigActionWidget(widgetClass, action, service, specAtt)
        }

        return holders
    }

    /**
     * 创建 property widget 实例
     *
     * 根据 widget class 是否有 [ValueList] 注解，选择不同的创建策略：
     * - 有 ValueList：为每个值项创建单独的 widget
     * - 无 ValueList：创建单个 widget 并设置值范围（如果有）
     *
     * @param widgetClass widget 类
     * @param property 属性规格
     * @param service 服务规格
     * @param specAtt 设备属性规格
     * @return 创建的 widget 持有者列表
     */
    private fun createPropertyWidget(
        widgetClass: MiwuWidgetClass,
        property: SpecAtt.Property,
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ): List<WidgetHolder> {
        return if (widgetClass.hasValueList()) {
            loadValueListWidgets(widgetClass, property, service, specAtt)
        } else {
            val widget = createAndConfigPropertyWidget(widgetClass, property, service, specAtt)
            property.valueRange?.let { widget.field.setValueRange(it[0], it[1], it[2]) }
            listOf(createHolder(widget))
        }
    }

    /**
     * 处理带 [ValueList] 注解的 widget
     *
     * 根据 [ValueList.pointTo] 的类型选择处理方式：
     * - [ValueList]：为每个值项创建单独的 widget
     * - 其他 KClass：递归加载该类型的 widget
     *
     * @param widgetClass widget 类
     * @param property 属性规格
     * @param service 服务规格
     * @param specAtt 设备属性规格
     * @return 创建的 widget 持有者列表
     */
    private fun loadValueListWidgets(
        widgetClass: MiwuWidgetClass,
        property: SpecAtt.Property,
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ): List<WidgetHolder> {
        return when (val pointTo = widgetClass.getPointTo()) {
            ValueList::class -> {
                property.valueList?.map { valueItem ->
                    val widget =
                        createAndConfigPropertyWidget(widgetClass, property, service, specAtt)
                    widget.field.apply {
                        desc = valueItem.description
                        descTranslation = valueItem.descriptionTranslation
                        serviceDesc = service.description
                        serviceDescTranslation = service.descriptionTranslation
                        setDefaultValue(valueItem.value)
                    }
                    createHolder(widget)
                } ?: emptyList()
            }

            else -> {
                runCatching {
                    pointTo as KClass<MiwuWidget<*>>
                    createPropertyWidget(pointTo.java, property, service, specAtt)
                }.getOrDefault(emptyList())
            }
        }
    }

    /**
     * 创建并配置 property widget 实例
     *
     * 配置内容：
     * - 公共字段（service 信息）
     * - property 特有字段（siid, piid, access 等）
     * - 属性值列表
     * - 多属性设置
     *
     * @param widgetClass widget 类
     * @param property 属性规格
     * @param service 服务规格
     * @param specAtt 设备属性规格
     * @return 配置好的 widget 实例
     */
    private fun createAndConfigPropertyWidget(
        widgetClass: MiwuWidgetClass,
        property: SpecAtt.Property,
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ): MiwuWidget<*> {
        return widgetClass.createWidgetInstance().apply {
            configureWidgetBase(service, specAtt)
            field.apply {
                siid = service.iid
                piid = property.iid
                propertyName = Urn.parseFrom(property.type).name
                desc = property.description
                descTranslation = property.descriptionTranslation
                valueOriginUnit = property.unit ?: ""
                allowWrite = "write" in property.access
                allowRead = "read" in property.access
                allowNotify = "notify" in property.access
                deviceType = this@WidgetLoader.deviceType
                property.valueList?.also { valueList.addAll(it) }
            }
        }
    }

    /**
     * 创建并配置 action widget 实例
     *
     * 配置内容：
     * - 公共字段（service 信息）
     * - action 特有字段（siid, aiid, actionName 等）
     *
     * @param widgetClass widget 类
     * @param action 动作规格
     * @param service 服务规格
     * @param specAtt 设备属性规格
     * @return 配置好的 widget 持有者
     */
    private fun createAndConfigActionWidget(
        widgetClass: MiwuWidgetClass,
        action: SpecAtt.Action,
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ): WidgetHolder {
        val widget = widgetClass.createWidgetInstance().apply {
            configureWidgetBase(service, specAtt)
            field.apply {
                siid = service.iid
                aiid = action.iid
                actionName = Urn.parseFrom(action.type).name
                desc = action.description
                descTranslation = action.descriptionTranslation
                deviceType = this@WidgetLoader.deviceType
            }
        }
        return createHolder(widget)
    }

    /**
     * 配置 widget 的公共字段
     *
     * 设置所有 widget 共有的字段：serviceName, serviceDesc, serviceDescTranslation。
     * 对于多属性 widget，还会设置 [SpecAtt] 引用。
     *
     * @param service 服务规格
     * @param specAtt 设备属性规格
     */
    private fun MiwuWidget<*>.configureWidgetBase(
        service: SpecAtt.Service,
        specAtt: SpecAtt
    ) {
        field.apply {
            serviceName = Urn.parseFrom(service.type).name
            serviceDesc = service.description
            serviceDescTranslation = service.descriptionTranslation
        }
        if (isMultiAttribute) field.miotSpecAtt = specAtt
    }

    /**
     * 创建 widget 持有者并注册到布局
     *
     * 执行以下操作：
     * 1. 设置 widget 的 icons 和 translateHelper
     * 2. 根据注解将 widget 添加到 layout 的对应区域
     * 3. 创建 WidgetHolder 并绑定
     *
     * @param widget widget 实例
     * @return 绑定后的 widget 持有者
     */
    private fun createHolder(widget: MiwuWidget<*>): WidgetHolder {
        widget.field.icons = icons
        widget.translateHelper = translateHelper
        addToLayout(widget)

        return WidgetHolder(widget).also { it.bind() }
    }

    /**
     * 根据注解将 widget 添加到 layout 的对应区域
     *
     * 支持的区域：Header, SubHeader, Body, SubFooter, Footer。
     * 未匹配到任何注解的 widget 添加到 unknown 区域。
     *
     * @param widget widget 实例
     */
    private fun addToLayout(widget: MiwuWidget<*>) {
        val widgetClass = widget.javaClass
        when (widgetClass.getPosition()) {
            is Header -> layout.header.add(widget)
            is SubHeader -> layout.subHeader.add(widget)
            is Body -> layout.body.add(widget)
            is SubFooter -> layout.subFooter.add(widget)
            is Footer -> layout.footer.add(widget)
            else -> layout.unknown.add(widget)
        }
    }

    /**
     * 查找 property 对应的 widget class 并校验是否支持
     *
     * @param serviceType service 的 URN 类型字符串
     * @param propertyType property 的 URN 类型字符串
     * @return 支持的 widget class，不支持则返回 null
     */
    private fun resolvePropertyWidget(
        serviceType: String,
        propertyType: String
    ): MiwuWidgetClass? {
        val widgetClass = PropertyRegistry.registry[
            Urn.parseFrom(serviceType).name to Urn.parseFrom(propertyType).name
        ]?.java ?: return null
        return (widgetClass as? MiwuWidgetClass)?.takeIf { it in supportWidget }
    }

    /**
     * 查找 action 对应的 widget class 并校验是否支持
     *
     * @param serviceType service 的 URN 类型字符串
     * @param actionType action 的 URN 类型字符串
     * @return 支持的 widget class，不支持则返回 null
     */
    private fun resolveActionWidget(
        serviceType: String,
        actionType: String
    ): MiwuWidgetClass? {
        val widgetClass = ActionRegistry.registry[
            Urn.parseFrom(serviceType).name to Urn.parseFrom(actionType).name
        ]?.java ?: return null
        return (widgetClass as? MiwuWidgetClass)?.takeIf { it in supportWidget }
    }

    /**
     * 检查 widget class 是否有 [ValueList] 注解
     *
     * @return true 表示有 ValueList 注解
     */
    private fun MiwuWidgetClass.hasValueList(): Boolean =
        annotations.any { it is ValueList }

    /**
     * 获取 [ValueList] 注解的 pointTo 类型
     *
     * @return pointTo 指定的 KClass，无注解时返回 [ValueList] 本身
     */
    private fun MiwuWidgetClass.getPointTo(): KClass<*> {
        val valueList = annotations.find { it is ValueList } as? ValueList
        return valueList?.pointTo ?: ValueList::class
    }

    /**
     * 获取 widget class 的位置注解
     *
     * @return 位置注解实例（Body/Footer/Header/SubHeader/SubFooter），无则返回 null
     */
    private fun MiwuWidgetClass.getPosition(): Any? =
        annotations.find { it is Body || it is Footer || it is Header || it is SubHeader || it is SubFooter }

    /**
     * 通过反射创建 widget 实例
     *
     * @return 新创建的 widget 实例
     */
    private fun MiwuWidgetClass.createWidgetInstance(): MiwuWidget<*> =
        getDeclaredConstructor().newInstance()
}
