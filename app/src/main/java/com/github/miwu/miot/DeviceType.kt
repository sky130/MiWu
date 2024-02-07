@file:Suppress("PackageDirectoryMismatch")

package com.github.miwu.miot.device

import android.content.Context
import android.view.ViewGroup
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.utils.getUnitString
import com.github.miwu.miot.widget.FanControl
import com.github.miwu.miot.widget.FanLevelControl
import com.github.miwu.miot.widget.FanSeekbar
import com.github.miwu.miot.widget.MiotBaseWidget
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

sealed class DeviceType(
    val device: MiotDevices.Result.Device,
    val layout: ViewGroup?,
    val manager: MiotDeviceManager?,
) {

    open val type = this::class.java.name.lowercase()// 设备类型
    open val isQuick = false// 是否支持快捷操作
    open val isMoreQuick: Boolean = false // 是否支持快捷操作
    open val isTextQuick: Boolean = false // 是否是文字
    open fun getQuickList(): ArrayList<out MiotBaseQuick>? = null
    open fun getQuick(): MiotBaseQuick? = null
    open fun getTextQuick(): MiotBaseQuick.TextQuick? = null
    val textPropertyList = arrayListOf<Pair<Int, SpecAtt.Service.Property>>()

    fun getBaseTextQuick()= MiotBaseQuick.TextQuick(device, textPropertyList)

    abstract fun onLayout(att: SpecAtt): DeviceType

    inline fun <reified V : MiotBaseWidget<*>> createView(
        siid: Int = -1,
        piid: Int = -1,
        property: SpecAtt.Service.Property? = null,
        action: SpecAtt.Service.Action? = null,
        index: Int = -1
    ) = manager?.createView<V>(layout!!, siid, piid)?.apply {
        property?.let { properties.add(siid to it) }
        action?.let { actions.add(siid to it) }
        manager.addView(this, index)
    }!!

    fun createFanControl(
        siid: Int,
        piid: Int = -1,
        property: SpecAtt.Service.Property,
        action: SpecAtt.Service.Action? = null,
        index: Int = -1
    ) {
        if (layout == null || manager == null) return
        val view = if (property.valueList != null) {
            FanLevelControl::class.java
        } else if (property.valueRange != null) {
            if (property.valueRange!![2] == 100) {
                FanSeekbar::class.java
            } else {
                FanControl::class.java
            }
        } else {
            null
        }
        if (view == null) return
        view.getDeclaredConstructor(
            Context::class.java
        ).newInstance(layout.context).apply {
            this.siid = siid
            this.piid = piid
            this.setManager(manager)
            properties.add(siid to property)
            action?.let { actions.add(siid to it) }
        }.apply {
            manager.addView(this, index)
        }
    }
}