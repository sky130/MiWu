package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.VacuumButtonBar
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class Vacuum(device: MiotDevices.Result.Device, layout: ViewGroup, manager: MiotDeviceManager) :
    DeviceType(device, layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false
    override fun getQuick() = null
    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    private val buttonBar by lazy { createView<VacuumButtonBar>() }

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "vacuum" to "status" -> {
                createView<StatusText>(siid, piid).apply {
                    properties.add(siid to obj)
                }
                buttonBar.siid = siid
                buttonBar.piid = piid
                buttonBar.apply {
                    properties.add(siid to obj)
                }
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
        when (service to action) {
            "vacuum" to "start-sweep" -> {
                buttonBar.actions.add(siid to obj)
            }

            "vacuum" to "stop-sweeping" -> {
                buttonBar.actions.add(siid to obj)
            }

            "battery" to "start-charge" -> {
                buttonBar.actions.add(siid to obj)
            }
        }
    }
}