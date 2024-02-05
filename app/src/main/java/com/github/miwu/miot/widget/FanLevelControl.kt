package com.github.miwu.miot.widget

import android.content.Context
import kndroidx.extension.compareTo
import kndroidx.extension.log
import com.github.miwu.databinding.MiotWidgetFanLevelBinding as Binding


class FanLevelControl(context: Context) : MiotBaseWidget<Binding>(context) {

    private val temperature get() = properties.first().second.valueList!!
    private var value: Int = 0
        set(value) {
            field = value
            binding.num <= value
        }

    override fun onValueChange(value: Any) {
        value as Int
        this.value = value
    }

    override fun init() {
        binding.up.setOnClickListener { up() }
        binding.down.setOnClickListener { down() }
        binding.numLarge <= temperature.last().value
    }

    fun up() {
        val index = temperature.indexOfFirst { it.value == value }
        if (index == temperature.size - 1) return
        value = temperature[index].value
        val obj = getPropertyWithSiid("fan-level")
        putValue(value, obj.first, obj.second.iid)
    }

    fun down() {
        val index = temperature.indexOfFirst { it.value == value }
        if (index == 0) return
        value = temperature[index].value
        val obj = getPropertyWithSiid("fan-level")
        putValue(value, obj.first, obj.second.iid)
    }


}