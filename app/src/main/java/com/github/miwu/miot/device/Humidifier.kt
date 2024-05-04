package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.initSpecAttFun
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.AirConditionerBar
import com.github.miwu.miot.widget.DehumidifierBar
import com.github.miwu.miot.widget.HumidifierBar
import com.github.miwu.miot.widget.SensorText
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.TemperatureControl
import kndroidx.extension.log
import kndroidx.extension.string
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("humidifier")
class Humidifier(
    device: MiotDevices.Result.Device,
    layout: ViewGroup?,
    manager: MiotDeviceManager?
) : DeviceType(device, layout, manager),
    SpecAttHelper {
    private val bar by lazy { createView<HumidifierBar>() }

    override val isSwitchQuick = false

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
            "humidifier" to "on" -> {
                bar.properties.add(siid to obj)
            }

            "humidifier" to "fan-level" -> {
                bar.properties.add(siid to obj)
            }

            "environment" to "temperature" -> {
                createView<SensorText>(siid, piid, obj, index = 0)
            }

            "environment" to "relative-humidity" -> {
                createView<SensorText>(siid, piid, obj, index = 0)
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
    ) = Unit
}