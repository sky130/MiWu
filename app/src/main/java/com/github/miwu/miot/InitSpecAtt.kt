package com.github.miwu.miot

import android.util.ArrayMap
import android.view.ViewGroup
import com.github.miwu.miot.device.*
import com.github.miwu.miot.manager.MiotDeviceManager
import kndroidx.KndroidX
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

// 未来考虑换成注解
fun initSpecAttFun(
    device: MiotDevices.Result.Device,
    mode: String,
    att: SpecAtt,
    layout: ViewGroup?,
    manager: MiotDeviceManager
): DeviceType? = when (mode.lowercase()) {
    "light" -> Light(device, layout, manager)

    "airer" -> Airer(device, layout, manager)

    "vacuum" -> Vacuum(device, layout, manager)

    "air-conditioner" -> AirConditioner(device, layout, manager)

    "temperature-humidity-sensor" -> HTSensor(device, layout, manager)

    "air-purifier" -> AirPurifier(device, layout, manager)

    "heater" -> Heater(device, layout, manager)

    "pet-feeder" -> Feeder(device, layout, manager)

    "switch" -> Switch(device, layout, manager)

    "outlet" -> Outlet(device, layout, manager)

    "dehumidifier" -> Dehumidifier(device, layout, manager)

    "camera" -> Camera(device, layout, manager)

    "speaker" -> Speaker(device, layout, manager)

    "gas-sensor" -> GasSensor(device, layout, manager)

    "magnet-sensor" -> MagnetSensor(device, layout, manager)

    "fan" -> Fan(device, layout, manager)

    "control-panel" -> ControlPanel(device, layout, manager)

    else -> {
        null
    }
}?.onLayout(att)

@Target(CLASS)
@Retention(RUNTIME)
annotation class SpecAttClass(val name: String)

val classList by lazy {
    ClassesReader.reader("com.github.miwu.miot.device", KndroidX.context)
        .filter { !it.name.contains("$") }
}
val classMap = ArrayMap<String, Class<*>>()

fun initClassList() {
    classList.forEach {
        it.annotations.forEach { annotation ->
            if (annotation is SpecAttClass) {
                classMap[annotation.name] = it
            }
        }
    }
}

fun initSpecAttByAnnotation(
    device: MiotDevices.Result.Device,
    mode: String,
    att: SpecAtt,
    layout: ViewGroup?,
    manager: MiotDeviceManager
): DeviceType? {
    (classMap[mode] ?: return null).apply {
        return (getDeclaredConstructor(
            MiotDevices.Result.Device::class.java,
            ViewGroup::class.java,
            MiotDeviceManager::class.java
        ).newInstance(device, layout, manager) as DeviceType).onLayout(att)
    }
}

fun getSpecAttByAnnotation(
    device: MiotDevices.Result.Device,
    mode: String,
    att: SpecAtt
): DeviceType? {
    (classMap[mode] ?: return null).apply {
        return (getDeclaredConstructor(
            MiotDevices.Result.Device::class.java,
            ViewGroup::class.java,
            MiotDeviceManager::class.java
        ).newInstance(device, null, null) as DeviceType).onLayout(att)
    }
}
