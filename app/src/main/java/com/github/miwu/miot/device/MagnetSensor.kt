package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.BoolStatusText
import com.github.miwu.miot.widget.SensorText
import com.github.miwu.miot.widget.StatusText
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("magnet-sensor")
class MagnetSensor(
    device: MiotDevices.Result.Device,
    layout: ViewGroup,
    manager: MiotDeviceManager
) :
    DeviceType(device, layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false
    override fun getQuick(): MiotBaseQuick? = null
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
            "magnet-sensor" to "contact-state" -> {
                createView<BoolStatusText>(siid, piid, obj)
            }

            "magnet-sensor" to "illumination" -> {
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