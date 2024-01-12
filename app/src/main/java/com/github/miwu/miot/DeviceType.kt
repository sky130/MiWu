@file:Suppress("PackageDirectoryMismatch")

package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.MiotDeviceManager
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.utils.Urn

sealed class DeviceType(val layout: ViewGroup,val manager: MiotDeviceManager) {

    open val type = this::class.java.name.lowercase()// 设备类型

    abstract val isQuickActionable: Boolean // 是否支持快捷操作

    abstract suspend fun onQuickAction()

    abstract fun onLayout(att: SpecAtt)

}