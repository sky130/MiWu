package com.github.miwu.miot.widget

import android.content.Context
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetStatusTextBinding
import kndroidx.extension.compareTo
import kndroidx.extension.string

class BoolStatusText(context: Context) : MiotBaseWidget<MiotWidgetStatusTextBinding>(context) {

    private val property by lazy { properties.first().second }

    override fun init() {
        binding.title <= "--"
        binding.subTitle <= property.description
    }

    override fun onValueChange(value: Any) {
        value as Boolean
        binding.title <= if (value) {
            R.string.enabled
        } else {
            R.string.disabled
        }.string
    }
}