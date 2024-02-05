package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.initSpecAttFun
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.AirConditionerBar
import com.github.miwu.miot.widget.TemperatureControl
import kndroidx.extension.log
import kndroidx.extension.string
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class AirConditioner(
    device: MiotDevices.Result.Device,
    layout: ViewGroup,
    manager: MiotDeviceManager
) : DeviceType(device, layout, manager),
    SpecAttHelper {
    private val bar by lazy { createView<AirConditionerBar>() }

    override val isQuickActionable = false

    override fun getQuick() = null

    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property
    ) {
        when (service to property) {
            "air-conditioner" to "on" -> {
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
        obj: SpecAtt.Service.Action
    ) {

    }
}