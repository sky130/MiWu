package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.SwitchQuick
import com.github.miwu.miot.widget.SensorText
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class Outlet(
    device: MiotDevices.Result.Device,
    layout: ViewGroup,
    manager: MiotDeviceManager
) : DeviceType(device, layout, manager),
    SpecAttHelper {

    override val isQuickActionable = true
    override val isMoreQuick = true
    override fun getQuick() = null
    val list = arrayListOf<SwitchQuick>()

    override fun getQuickList() = list

    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property
    ) {
        when (service to property) {
            "switch" to "on" -> {
                SwitchQuick(device, siid, piid)
                createView<Switch>(siid, piid, obj)
            }

            "power-consumption" to "surge-power" -> {
                createView<SensorText>(siid, piid, obj, index = 0)
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