package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetSwitchBinding
import kndroidx.extension.compareTo


class Switch(context: Context) : MiotBaseWidget<MiotWidgetSwitchBinding>(context) {
    private var value = false
        set(value) {
            refreshStatus(value)
            field = value
        }

    init {
        setOnClickListener {
            value = !value
            putValue(value)
        }
    }

    private fun refreshStatus(value: Boolean) =binding.apply {
        if (value) {
            img.setBackgroundResource(R.drawable.bg_switch_button_on)
            title <= "关闭"
        } else {
            img.setBackgroundResource(R.drawable.bg_switch_button_off)
            title <= "开启"
        }
    }

    override fun onValueChange(value: Any) {
        value as Boolean
        this.value = value
    }

}