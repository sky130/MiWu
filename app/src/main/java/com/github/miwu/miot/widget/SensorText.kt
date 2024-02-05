package com.github.miwu.miot.widget

import android.content.Context
import com.github.miwu.databinding.MiotWidgetSensorHtTextBinding
import com.github.miwu.miot.utils.getUnitString
import kndroidx.extension.compareTo

class SensorText(context: Context) : MiotBaseWidget<MiotWidgetSensorHtTextBinding>(context) {

    private val property get() = properties.first().second

    override fun init() {
        binding.value <= "--"
        binding.unit <= getUnitString(property.unit)
        binding.desc <= getUnitString(property.description)
    }

    override fun onValueChange(value: Any) {
        binding.value <= value.toString()
    }

}