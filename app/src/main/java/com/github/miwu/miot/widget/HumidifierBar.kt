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

    private val levelList get() = getProperty("fan-level").valueList!!

    private var level = 1
        set(value) {
            field = value
            setLevelDebug(value)
        }


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

            // 如果当前索引对应的元素的值为 4，则再增加一次索引，跳过该元素
            if (index < levelList.size && levelList[index].value == 4) {
                index++
            }
            // 如果索引超出了列表范围，则重置为 0
            if (index >= levelList.size) {
                index = 0
            }//该段为暂时屏蔽没有用的恒湿模式使用，后续如果上线恒湿可以删掉

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