package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.SensorText
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("temperature-humidity-sensor")
class HTSensor(device: MiotDevices.Result.Device, layout: ViewGroup, manager: MiotDeviceManager) :
    DeviceType(device, layout, manager),
    SpecAttHelper {



    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        serviceDesc:String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "temperature-humidity-sensor" to "temperature" -> {
                createView<SensorText>(siid, piid, obj)
            }

            "temperature-humidity-sensor" to "relative-humidity" -> {
                createView<SensorText>(siid, piid, obj)
            }

            "battery" to "battery-level" -> {
                createView<SensorText>(siid, piid, obj)
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
    ) {

    }

}