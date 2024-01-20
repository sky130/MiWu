package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.miot.widget.StatusText
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class Airer(device: MiotDevices.Result.Device,layout: ViewGroup, manager: MiotDeviceManager) : DeviceType(device,layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false

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
            "airer" to "fault" -> {
                createView<StatusText>(siid, piid)
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