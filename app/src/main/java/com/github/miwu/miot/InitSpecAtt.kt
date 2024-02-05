package com.github.miwu.miot

import android.view.ViewGroup
import com.github.miwu.miot.device.*
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
): DeviceType? = when (mode.lowercase()) {
    "light" -> Light(device, layout, manager)

    "airer" -> Airer(device, layout, manager)

    "vacuum" -> Vacuum(device, layout, manager)

    "air-conditioner" -> AirConditioner(device, layout, manager)

    "temperature-humidity-sensor" -> SensorHT(device, layout, manager)

    "air-purifier" -> AirPurifier(device, layout, manager)

    "heater" -> Heater(device, layout, manager)

    "pet-feeder" -> Feeder(device, layout, manager)

    "switch" -> Switch(device, layout, manager)

    "outlet" -> Outlet(device, layout, manager)

    "dehumidifier" -> Dehumidifier(device, layout, manager)

    "camera" -> Camera(device, layout, manager)

    "speaker" -> Speaker(device, layout, manager)

    else -> {
        null
    }
}?.onLayout(att)

