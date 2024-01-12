package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.widget.Switch
import kndroidx.extension.log
import miot.kotlin.model.att.SpecAtt

class Light(layout: ViewGroup, manager: MiotDeviceManager) : DeviceType(layout, manager),
    SpecAttHelper {

    override val isQuickActionable = true

    override suspend fun onQuickAction() = TODO("Not yet implemented")

    override fun onLayout(att: SpecAtt) = initAtt(att)

    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property,
    ) {
        when (service to property) {
            "light" to "on" -> {
                "$service $property".log.d()
                manager.addView(
                    manager.createView<Switch>(layout, siid, piid)
                )
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