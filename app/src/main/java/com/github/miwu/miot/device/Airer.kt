package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.AirPurifierBar
import com.github.miwu.miot.widget.AirerBar
import com.github.miwu.miot.widget.SensorHTText
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class Airer(device: MiotDevices.Result.Device, layout: ViewGroup, manager: MiotDeviceManager) :
    DeviceType(device, layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false
    private val bar by lazy { createView<AirerBar>() }

    override fun getQuick(): MiotBaseQuick? = null
    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "light" to "on" -> {
                createView<Switch>(siid, piid, index = 1)
            }

            "airer" to "motor-control" -> {
                bar.properties.add(siid to obj)
            }

            "airer" to "status" -> {
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
        obj: SpecAtt.Service.Action,
    ) {

    }

}