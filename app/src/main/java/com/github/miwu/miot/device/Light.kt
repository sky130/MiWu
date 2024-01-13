package com.github.miwu.miot.device

import android.view.ViewGroup
import com.github.miwu.miot.MiotDeviceManager
import com.github.miwu.miot.SpecAttHelper
import com.github.miwu.miot.widget.BrightnessSeekBar
import com.github.miwu.miot.widget.ColorTemperatureSeekbar
import com.github.miwu.miot.widget.Switch
import miot.kotlin.model.att.SpecAtt

class Light(layout: ViewGroup, manager: MiotDeviceManager) : DeviceType(layout, manager),
    SpecAttHelper {

    override val isQuickActionable = true

    override suspend fun onQuickAction() = TODO("Not yet implemented")

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
                createAddView<Switch>(siid, piid, obj)
            }

            "light" to "brightness" -> {
                createAddView<BrightnessSeekBar>(siid, piid, obj)
            }

            "light" to "color-temperature" -> {
                createAddView<ColorTemperatureSeekbar>(siid, piid, obj)
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