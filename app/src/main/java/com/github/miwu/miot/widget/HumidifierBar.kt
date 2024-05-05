package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetAirConditionerBarBinding as Binding
import kndroidx.extension.compareTo
import kndroidx.extension.log
import miot.kotlin.utils.parseUrn


class HumidifierBar(context: Context) : MiotBaseWidget<Binding>(context) {

    private var on = false
        set(value) {
            field = value
            refreshOn(value)
        }
    private var level = 1
        set(value) {
            field = value
            setLevelDebug(value)
        }
    private val levelList = getProperty("fan-level").valueList!!


    override fun init() {
        binding.on.setOnClickListener {
            on = !on
            val obj = getPropertyWithSiid("on")
            putValue(on, obj.first, obj.second.iid)
        }
        binding.mode.setOnClickListener {
            var index = levelList.indexOfFirst { it.value == level }
            if (index == levelList.size - 1) {
                index = 0
            } else {
                index++
            }
            val obj = getPropertyWithSiid("fan-level")
            this.level = levelList[index].value
            putValue(level, obj.first, obj.second.iid)
        }
    }

    private fun setLevelDebug(value: Int) {
        binding.modeText.text = levelList.first { it.value == value }.description
    }

    private fun refreshOn(value: Boolean) {
        if (value) {
            binding.on.setBackgroundResource(R.drawable.bg_item_blue)
        } else {
            binding.on.setBackgroundResource(R.drawable.bg_item)
        }
    }


    override fun onValueChange(siid: Int, piid: Int, value: Any) {
        when (getPropertyName(piid)) {
            "on" -> {
                on = value as Boolean
            }

            "fan-level" -> {
                level = (value as Double).toInt()
            }
        }
    }

    override fun onValueChange(value: Any) = Unit

}