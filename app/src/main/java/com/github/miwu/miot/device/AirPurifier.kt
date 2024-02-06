package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.widget.AirPurifierBar
import com.github.miwu.miot.widget.SensorText
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("air-purifier")
class AirPurifier(
    device: MiotDevices.Result.Device,
    layout: ViewGroup,
    manager: MiotDeviceManager
) : DeviceType(device, layout, manager),
    SpecAttHelper {
    private val bar by lazy { createView<AirPurifierBar>() }

    override val isQuickActionable = false

    override fun getQuick() = null

    override fun onLayout(att: SpecAtt) = forEachAtt(att)

override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        serviceDesc:String,
        obj: SpecAtt.Service.Property,
    ){
        when (service to property) {
            "air-purifier" to "on" -> {
                bar.properties.add(siid to obj)
            }

            "air-purifier" to "mode" -> {
                bar.properties.add(siid to obj)
            }

            "environment" to "pm2.5-density" -> {
                createView<SensorText>(siid, piid,obj, index = 0)
            }
        }
    }

override fun onActionFound(
        siid: Int,
        service: String,
        aiid: Int,
        action: String,
        serviceDesc:String,
        obj: SpecAtt.Service.Action,
    ){

    }
}