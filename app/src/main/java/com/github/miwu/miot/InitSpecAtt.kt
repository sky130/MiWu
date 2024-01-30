package com.github.miwu.miot

import android.view.ViewGroup
import com.github.miwu.miot.device.AirConditioner
import com.github.miwu.miot.device.Airer
import com.github.miwu.miot.device.DeviceType
import com.github.miwu.miot.device.Light
import com.github.miwu.miot.device.Vacuum
import com.github.miwu.miot.manager.MiotDeviceManager
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

// 未来考虑换成注解
fun initSpecAttFun(
    device: MiotDevices.Result.Device,
    mode: String,
    att: SpecAtt,
    layout: ViewGroup,
    manager: MiotDeviceManager
): DeviceType? =
    when (mode) {
        "light" -> {
            Light(device, layout, manager)
        }

        "airer" -> {
            Airer(device, layout, manager)
        }

        "vacuum" -> {
            Vacuum(device, layout, manager)
        }

        "air-conditioner" -> {
            AirConditioner(device, layout, manager)
        }

        else -> {
            null
        }
    }?.onLayout(att)

