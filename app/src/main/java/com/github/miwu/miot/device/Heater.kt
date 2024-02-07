package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.widget.AirConditionerBar
import com.github.miwu.miot.widget.HeaterBar
import com.github.miwu.miot.widget.Switch
import com.github.miwu.miot.widget.TemperatureControl
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("heater")
class Heater(
    device: MiotDevices.Result.Device,
    layout: ViewGroup,
    manager: MiotDeviceManager
) : DeviceType(device, layout, manager),
    SpecAttHelper {
    private val bar by lazy { createView<HeaterBar>() }

    override val isQuick = false

    override fun getQuick() = null

    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "heater" to "on" -> {
                createView<Switch>(siid, piid, obj)
            }

            "heater" to "target-temperature" -> {
                createView<TemperatureControl>(siid, piid, obj)
            }

            "heater" to "mode" -> {
                bar.properties.add(siid to obj)
            }

            "fan-control" to "horizontal-swing" -> {
                bar.properties.add(siid to obj)
            }
        }
    }

    override fun onActionFound(
        siid: Int,
        service: String,
        aiid: Int,
        action: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Action,
    ) {

    }
}