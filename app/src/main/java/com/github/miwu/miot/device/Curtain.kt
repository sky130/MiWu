package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.SpecAttClass
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.manager.MiotDeviceManager
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

@SpecAttClass("curtain")
class Curtain(device: MiotDevices.Result.Device, layout: ViewGroup?, manager: MiotDeviceManager?) :
    DeviceType(device, layout, manager), SpecAttHelper {

    override fun onLayout(att: SpecAtt) = forEachAtt(att)


    override fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Property
    ) {
    }

    override fun onActionFound(
        siid: Int,
        service: String,
        aiid: Int,
        action: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Action
    ) {

    }
}