package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.quick.SwitchQuick
import com.github.miwu.miot.widget.SensorText
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("outlet")
class Outlet(
    device: MiotDevices.Result.Device,
    layout: ViewGroup?,
    manager: MiotDeviceManager?
) : DeviceType(device, layout, manager),
    SpecAttHelper {

    override val isSwitchQuick = ::quick.isInitialized
    override fun getQuick() = SwitchQuick(device, quick.first, quick.second)
    private lateinit var quick: Pair<Int, Int>
    val list = arrayListOf<SwitchQuick>()

    override fun getQuickList() = list

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
            "switch" to "on" -> {
                quick = siid to piid
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
        serviceDesc: String,
        obj: SpecAtt.Service.Action,
    ) {

    }
}