package miwu.support.layout

import miwu.support.MiwuWidget

typealias WidgetList = ArrayList<MiwuWidget<*>>

/**
 * `miwu-support` 布局容器，用于按区域组织和管理 [MiwuWidget]
 *
 * 该布局将界面划分为七个区域 [WidgetList]，分别存储不同类型的 [MiwuWidget]：
 * - 头部(header)：顶部主要区域，通常用于导航栏、标题栏等
 * - 头部面板(headerPanel)：头部下方的扩展面板区域
 * - 控件面板(controlPanel)：控件区域，用于滑块、开关条等
 * - 主体(body)：中间主要内容区域
 * - 底部面板(footerPanel)：底部上方的扩展面板区域
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
 * @property headerPanel 头部面板
 * @property controlPanel 控件面板
 * @property body 主体
 * @property footerPanel 底部面板
 * @property footer 尾部
 * @property unknown 未分类
 */
data class MiwuWidgetLayout(
    val header: WidgetList = arrayListOf(),
    val headerPanel: WidgetList = arrayListOf(),
    val controlPanel: WidgetList = arrayListOf(),
    val body: WidgetList = arrayListOf(),
    val footerPanel: WidgetList = arrayListOf(),
    val footer: WidgetList = arrayListOf(),
    val unknown: WidgetList = arrayListOf(),
) {
    /**
     * 清空所有区域的组件列表。
     *
     * 调用此方法将移除所有区域中的所有组件。
     */
    fun clear() {
        header.clear()
        headerPanel.clear()
        controlPanel.clear()
        body.clear()
        footerPanel.clear()
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
     * 遍历头部面板组件并对每个组件执行指定操作。
     *
     * @param block 对每个头部面板组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun HeaderPanel(block: (MiwuWidget<*>) -> Unit) {
        headerPanel.forEach { block(it) }
    }

    /**
     * 遍历控件面板组件并对每个组件执行指定操作。
     *
     * @param block 对每个控件面板组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun ControlPanel(block: (MiwuWidget<*>) -> Unit) {
        controlPanel.forEach { block(it) }
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
     * 遍历底部面板组件并对每个组件执行指定操作。
     *
     * @param block 对每个底部面板组件执行的操作，接收一个[MiwuWidget]参数
     */
    fun FooterPanel(block: (MiwuWidget<*>) -> Unit) {
        footerPanel.forEach { block(it) }
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