package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetAirerBarBinding as Binding
import kndroidx.extension.compareTo
import miot.kotlin.utils.parseUrn


class AirerBar(context: Context) : MiotBaseWidget<Binding>(context) {
    private val modeList get() = getProperty("motor-control").valueList!!
    private var status = 0
        set(value) {
            field = value
            refreshMode(value)
        }

    private fun getModeDesc(value: Int) =
        modeList.find { it.value == value }!!.description

    override fun init() = binding.run {
        val control = getPropertyWithSiid("motor-control")
        up.setOnClickListener {
            status = if (status != 1) {
                1
            } else {
                0
            }
            putValue(status, control.first, control.second.iid)
        }
        down.setOnClickListener {
            status = if (status != 2) {
                2
            } else {
                0
            }
            putValue(status, control.first, control.second.iid)
        }
    }

    private fun refreshMode(value: Int) {
        binding.up.setBackgroundResource(R.drawable.bg_item)
        binding.down.setBackgroundResource(R.drawable.bg_item)
        if (value == 1) {
            binding.up.setBackgroundResource(R.drawable.bg_item_blue)
        } else if (value == 2) {
            binding.down.setBackgroundResource(R.drawable.bg_item_blue)
        }
    }

    override fun onValueChange(siid: Int, piid: Int, value: Any) {
        value as Double
        when (getPropertyName(piid)) {
            "status" -> {
                status = value.toInt()
            }
        }
    }

    override fun onValueChange(value: Any) = Unit

}