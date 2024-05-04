package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.AirerBar
import com.github.miwu.miot.widget.CurtainBar
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("curtain")
class Curtain(device: MiotDevices.Result.Device, layout: ViewGroup?, manager: MiotDeviceManager?) :
    DeviceType(device, layout, manager), SpecAttHelper {
    private val bar by lazy { createView<CurtainBar>() }

    override fun getQuick(): MiotBaseQuick? = null
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
            "curtain" to "motor-control" -> {
                bar.properties.add(siid to obj)
            }

            "curtain" to "status" -> {
                bar.properties.add(siid to obj)
                createView<StatusText>(siid, piid, obj, index = 0)
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