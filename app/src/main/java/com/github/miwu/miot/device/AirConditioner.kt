package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.SwitchQuick
import com.github.miwu.miot.widget.AirConditionerBar
import com.github.miwu.miot.widget.TemperatureControl
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("air-conditioner")
class AirConditioner(
    device: MiotDevices.Result.Device,
    layout: ViewGroup?,
    manager: MiotDeviceManager?
) : DeviceType(device, layout, manager),
    SpecAttHelper {
    private val bar by lazy { createView<AirConditionerBar>() }

    override val isSwitchQuick = true
    private lateinit var pair: Pair<Int, Int>
    override fun getQuick() = SwitchQuick(device, pair.first, pair.second)

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
            "air-conditioner" to "on" -> {
                pair = siid to piid
                bar.properties.add(siid to obj)
            }

            "air-conditioner" to "mode" -> {
                bar.properties.add(siid to obj)
            }

            "air-conditioner" to "target-temperature" -> {
                createView<TemperatureControl>(siid, piid, property = obj, index = 0)
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