package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.LightQuick
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.quick.SwitchQuick
import com.github.miwu.miot.widget.BrightnessSeekBar
import com.github.miwu.miot.widget.ColorTemperatureSeekbar
import com.github.miwu.miot.widget.SensorText
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("control-panel")
class ControlPanel(device: MiotDevices.Result.Device, layout: ViewGroup, manager: MiotDeviceManager) :
    DeviceType(device, layout, manager), SpecAttHelper {

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
    ) {
        when (service to property) {
            "switch" to "on" -> {
                SwitchQuick(device, siid, piid)
                obj.description = serviceDesc
                createView<Switch>(siid, piid, obj)
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
        serviceDesc:String,
        obj: SpecAtt.Service.Action,
    ) {

    }

}