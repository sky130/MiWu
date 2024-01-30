package com.github.miwu.miot.widget

import android.content.Context
import kndroidx.extension.compareTo
import com.github.miwu.databinding.MiotWidgetAirConditionerTemperatureBinding as Binding


class TemperatureControl(context: Context) : MiotBaseWidget<Binding>(context) {

    private val temperature get() = properties[0].second
    private var value: Float = 0f
    private val step = temperature.valueRange!![2].toFloat()
    private val max = temperature.valueRange!![1].toFloat()
    private val min = temperature.valueRange!![0].toFloat()


    override fun onValueChange(value: Any) {
        value as Float
        this.value = value
        binding.num <= value
    }

    override fun init() {
        binding.up.setOnClickListener { up() }
        binding.down.setOnClickListener { down() }
    }

    fun up() {
        if (value >= max) return
        onValueChange(value + step)
        putValue(value)
    }

    fun down() {
        if (value <= min) return
        onValueChange(value - step)
        putValue(value)
    }


}