package com.github.miwu.miot.widget

import android.content.Context
import kndroidx.extension.compareTo
import com.github.miwu.databinding.MiotWidgetFanLevelBinding as Binding


class FanControl(context: Context) : MiotBaseWidget<Binding>(context) {

    private val temperature get() = properties[0].second
    private var value: Double = 0.0
    private val step get() = temperature.valueRange!![2].toFloat()
    private val max get() = temperature.valueRange!![1].toFloat()
    private val min get() = temperature.valueRange!![0].toFloat()

    override fun onValueChange(value: Any) {
        value as Double
        this.value = value
        binding.num <= if (value.toString().endsWith(".0")){
            value.toInt()
        }else{
            value
        }
    }

    override fun init() {
        binding.up.setOnClickListener { up() }
        binding.down.setOnClickListener { down() }
        binding.numLarge <= max.toInt()
    }

    fun up() {
        if (value >= max) return
        value += step
        onValueChange(value)
        putValue(value)
    }

    fun down() {
        if (value <= min) return
        value -= step
        onValueChange(value)
        putValue(value)
    }


}