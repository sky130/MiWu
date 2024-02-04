package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetHeaterBarBinding as Binding
import kndroidx.extension.compareTo
import miot.kotlin.utils.parseUrn


class HeaterBar(context: Context) : MiotBaseWidget<Binding>(context) {
    private var on = false
        set(value) {
            field = value
            refreshOn(value)
            val obj = getPropertyWithSiid("horizontal-swing")
            putValue(value, obj.first, obj.second.iid)
        }
    private val modeList get() = getProperty("mode").valueList!!
    private var modeValue = 0
        set(value) {
            field = value
            refreshMode(value)
            val obj = getPropertyWithSiid("mode")
            putValue(value, obj.first, obj.second.iid)
        }

    private fun getModeDesc(value: Int) =
        modeList.find { it.value == value }!!.description


    override fun init() {
        modeValue = modeList.first().value
        binding.on.setOnClickListener {
            on = !on
        }

        binding.mode.setOnClickListener {
            var index = modeList.indexOfFirst { it.value == modeValue }
            if (index == modeList.size - 1) {
                index = 0
            } else {
                index++
            }
            modeValue = modeList[index].value
        }
    }


    private fun refreshOn(value: Boolean) {
        if (value) {
            binding.on.setBackgroundResource(R.drawable.bg_item_blue)
        } else {
            binding.on.setBackgroundResource(R.drawable.bg_item)
        }
    }

    private fun refreshMode(value: Int) {
        binding.modeText <= getModeDesc(value)
    }

    override fun onValueChange(siid: Int, piid: Int, value: Any) {
        when (getPropertyName(piid)) {
            "horizontal-swing" -> {
                on = value as Boolean
            }

            "mode" -> {
                modeValue = 0
                refreshMode((value as Double).toInt())
            }
        }
    }

    override fun onValueChange(value: Any) = Unit

}