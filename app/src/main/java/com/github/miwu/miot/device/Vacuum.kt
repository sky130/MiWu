package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.widget.StatusText
import com.github.miwu.miot.widget.VacuumButtonBar
import miot.kotlin.model.att.SpecAtt

class Vacuum(layout: ViewGroup, manager: MiotDeviceManager) : DeviceType(layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false
    override suspend fun onQuickAction() {}
    override fun onLayout(att: SpecAtt) = forEachAtt(att)

    private val buttonBar by lazy { createAddView<VacuumButtonBar>() }

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "vacuum" to "status" -> {
                createAddView<StatusText>(siid, piid, obj)
                buttonBar.siid = siid
                buttonBar.piid = piid
                buttonBar.property = obj
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