@file:Suppress("UNCHECKED_CAST")

package miwu.support.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import miwu.icon.Icons
import miwu.miot.client.MiotDeviceClient
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.api.Cache
import miwu.support.base.MiwuWidget
import miwu.support.layout.MiwuLayout
import miwu.support.translate.TranslateHelper

abstract class MiotDeviceManager {

    /**
     * [MiwuWidget] 的布局容器
     *
     * 执行初始化 [init] 之后, 可在这里获取初始化结果
     */
    abstract val layout: MiwuLayout

    /**
     * 初始化 [MiotDeviceManager] 和创建 [MiwuLayout], 并且开始遍历获取服务器上的设备信息
     */
    abstract fun init()

    /**
     * 结束 [MiotDeviceManager] 的运行, 释放并回收所有 [MiwuWidget]
     *
     * 如果要结束使用 [MiotDeviceManager], 必须执行该方法, 否则可能会造成内存泄露
     */
    abstract fun stop()

    /**
     * 向服务器请求, 更新设备的数据, 同时向绑定的 [MiwuWidget] 推送新属性
     *
     * @param siid 服务实例 ID
     * @param piid 属性实例 ID
     * @param value 属性值
     */
    abstract fun updateValue(siid: Int, piid: Int, value: Any)

    /**
     * 向服务器请求, 执行设备动作
     *
     * @param siid 服务实例 ID
     * @param aiid 动作实例 ID
     * @param input 动作参数
     */
    abstract fun doAction(siid: Int, aiid: Int, vararg input: Any)

    companion object {
        /**
         * 构建 [MiotDeviceManager]
         *
         * @param miot 用于调用 MiotClient 接口
         * @param specAttrProvider 用于调用 MiotManager 接口
         * @param device 设备详情
         * @param icons 图标库
         * @param cache 用于缓存设备属性
         * @param translateHelper 用于翻译
         * @param dispatcher 用于更新 UI 数据的线程
         * @param callback 用于回调设备初始化状态
         *
         * @return [MiotDeviceManager]
         */
        fun build(
            miot: MiotDeviceClient,
            specAttrProvider: MiotSpecAttrProvider,
            device: MiotDevice,
            icons: Icons,
            cache: Cache,
            translateHelper: TranslateHelper,
            dispatcher: CoroutineDispatcher = Dispatchers.Default,
            callback: Callback? = null
        ): MiotDeviceManager = MiotDeviceManagerImpl(
            miot,
            specAttrProvider,
            device,
            icons,
            cache,
            translateHelper,
            dispatcher,
            callback
        )
    }

    interface Callback {
        fun onDeviceInitiated()

        fun onDeviceAttLoaded(specAtt: SpecAtt) = Unit
    }
}

