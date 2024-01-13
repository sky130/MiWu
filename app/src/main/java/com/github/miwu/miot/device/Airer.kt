package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.widget.StatusText
import miot.kotlin.model.att.SpecAtt

class Airer(layout: ViewGroup, manager: MiotDeviceManager) : DeviceType(layout, manager),
    SpecAttHelper {
    override val isQuickActionable = false

    override suspend fun onQuickAction() {}

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
                createAddView<StatusText>(siid, piid, obj)
            }
        }
    }

    override fun onActionFound(
        siid: Int,
        service: String,
        piid: Int,
        action: String,
        obj: SpecAtt.Service.Action,
    ) {

    }

}