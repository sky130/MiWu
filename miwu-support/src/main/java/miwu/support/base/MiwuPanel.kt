package miwu.support.base

import miwu.miot.model.att.SpecAtt.Service.Property
import miwu.support.manager.MiwuWidgetClass

/**
 * MiwuPanel
 *
 * 对于 Panel 的设想是, 用于解构某个属性, 它不属于组件
 *
 * 就拿`窗帘`来说, 窗帘的 `motor-control` 属性, 有三种状态,
 *
 * - `close` 关闭窗帘
 * - `open` 打开窗帘
 * - `pause` 暂停
 *
 * 应该使用两个按钮来展示个属性, 一个是打开, 一个是关闭, 当对应状态时就高亮显示
 *
 * 这个工作就是使用 [MiwuPanel] 来完成, 将解析任务从 [miwu.support.manager.MiotDeviceManager] 剥离
 *
 * 由 [MiwuPanel] 来解析 `motor-control` 属性
 *
 * 对应的例子
 * ``` kotlin
 * @Widget
 * @Body
 * @Bind<Property>("curtain", "motor-control")
 * class CurtainPanel : MiwuPanel<Int>() {
 *     override fun onCreateWidget() = scope {
 *         valueList
 *             .filter { it.description == "Pause" }
 *             .forEach { value ->
 *                 create<ModeButton> {
 *                     defaultValue(value)
 *                 }
 *             }
 *     }
 * }
 * ```
 *
 * @param T Widget 的类型
 */
abstract class MiwuPanel<T> : MiwuWidget<T>() {
    internal val widgetList = mutableListOf<Pair<MiwuWidgetClass, MiwuWidget<*>>>()

    abstract fun onCreateWidget(): PanelBuildScope

    fun scope(content: PanelBuildScope.() -> Unit) =
        PanelBuildScope().apply {
            runCatching {
                content()
            }
        }

    fun list(): List<Pair<MiwuWidgetClass, MiwuWidget<*>>> = widgetList.toList()

    inner class PanelBuildScope internal constructor() {

        @Suppress("UNCHECKED_CAST")
        inline fun <reified Widget : MiwuWidget<T>> create(block: Widget.() -> Unit) {
            val clazz = (Widget::class.java as? MiwuWidgetClass) ?: return
            runCatching {
                clazz.getDeclaredConstructor()
                    .newInstance()
                    .let { it as? Widget }
                    ?.apply(::configure)
                    ?.apply(block)
                    ?.also { add(it, clazz) }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun MiwuWidget<T>.defaultValue(
            value: Property.Value,
            special: Property.Value? = null
        ) {
            runCatching {
                with(field) {
                    desc = value.description
                    descTranslation = value.descriptionTranslation
                    setDefaultValue(value.value, special?.value)
                }
            }
        }

        fun drop(): Nothing = error("Drop the widget")

        fun configure(widget: MiwuWidget<*>) {
            val field = this@MiwuPanel.field
            with(widget.field) {
                siid = field.siid
                piid = field.piid
                propertyName = field.propertyName
                serviceName = field.serviceName
                desc = field.desc
                valueOriginUnit = field.valueOriginUnit
                serviceDesc = field.serviceDesc
                serviceDescTranslation = field.serviceDescTranslation
                descTranslation = field.descTranslation
                allowWrite = field.allowWrite
                allowRead = field.allowRead
                allowNotify = field.allowNotify
                valueList.addAll(field.valueList)
            }
        }

        fun add(widget: MiwuWidget<*>, widgetClass: MiwuWidgetClass) {
            widgetList.add(widgetClass to widget)
        }
    }
}