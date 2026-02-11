package miwu.support.layout

import miwu.support.base.MiwuWidget

typealias WidgetList = ArrayList<MiwuWidget<*>>

/**
 * `miwu-support` 布局容器，用于按区域组织和管理[MiwuWidget]
 *
 * 该布局将界面划分为六个区域 [WidgetList]，分别存储不同类型的 [MiwuWidget]：
 * - 头部(header)：顶部主要区域，通常用于导航栏、标题栏等
 * - 子头部(subHeader)：头部下方区域，用于次级导航或工具栏
 * - 主体(body)：中间主要内容区域
 * - 子尾部(subFooter)：尾部上方区域，用于辅助操作按钮等
 * - 尾部(footer)：底部区域，通常用于底部导航栏等
 * - 未知(unknown)：未分类或临时存放的组件
 *
 * 示例用法：
 * ```
 * val layout = MiwuLayout()
 *
 * with(layout) {
 *     header.add(...) // widget here
 * }
 * ```
 *
 * @property header 头部
 * @property subHeader 子头部
 * @property body 主体
 * @property subFooter 子尾部
 * @property footer 尾部
 * @property unknown 未分类
 */
data class MiwuLayout(
    val header: WidgetList = arrayListOf(),
    val subHeader: WidgetList = arrayListOf(),
    val body: WidgetList = arrayListOf(),
    val subFooter: WidgetList = arrayListOf(),
    val footer: WidgetList = arrayListOf(),
    val unknown: WidgetList = arrayListOf(),
) {

    val isEmpty
        get() = listOf(
            header,
            subHeader,
            body,
            subFooter,
            footer,
            unknown
        ).all { it.isEmpty() }

    /**
     * 清空所有区域的组件列表。
     *
     * 调用此方法将移除header、subHeader、body、subFooter、footer和unknown中的所有组件。
     */
    fun clear() {
        header.clear()
        subHeader.clear()
        body.clear()
        subFooter.clear()
        footer.clear()
        unknown.clear()
    }

    /**
     * 遍历头部组件并对每个组件执行指定操作。
     *
     * @param block 对每个头部组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun Header(block: (MiwuWidget<*>) -> Unit) {
        header.forEach { block(it) }
    }

    /**
     * 遍历子头部组件并对每个组件执行指定操作。
     *
     * @param block 对每个子头部组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun SubHeader(block: (MiwuWidget<*>) -> Unit) {
        subHeader.forEach { block(it) }
    }

    /**
     * 遍历主体组件并对每个组件执行指定操作。
     *
     * @param block 对每个主体组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun Body(block: (MiwuWidget<*>) -> Unit) {
        body.forEach { block(it) }
    }

    /**
     * 遍历子尾部组件并对每个组件执行指定操作。
     *
     * @param block 对每个子尾部组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun SubFooter(block: (MiwuWidget<*>) -> Unit) {
        subFooter.forEach { block(it) }
    }

    /**
     * 遍历尾部组件并对每个组件执行指定操作。
     *
     * @param block 对每个尾部组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun Footer(block: (MiwuWidget<*>) -> Unit) {
        footer.forEach { block(it) }
    }

    /**
     * 遍历未分类组件并对每个组件执行指定操作。
     *
     * @param block 对每个未分类组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun Unknown(block: (MiwuWidget<*>) -> Unit) {
        unknown.forEach { block(it) }
    }
}