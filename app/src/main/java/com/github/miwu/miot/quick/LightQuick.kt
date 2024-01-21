package com.github.miwu.miot.quick

import com.github.miwu.MainApplication.Companion.miot
import kotlinx.coroutines.withContext
import miot.kotlin.helper.SetAtt
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

class LightQuick(
    device: MiotDevices.Result.Device,
    siid: Int,
    piid: Int,
) : MiotBaseQuick.DeviceQuick<Boolean>(device, siid, piid) {

    override var value = false

    override fun initValue() {
        value = !value
    }

    override suspend fun doAction() {
        miot.setDeviceAtt(device, arrayOf(SetAtt(siid, piid, value)))
    }

}