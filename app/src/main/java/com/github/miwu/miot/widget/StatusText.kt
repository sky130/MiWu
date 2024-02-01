package com.github.miwu.miot.widget

import android.content.Context
import com.github.miwu.databinding.MiotWidgetStatusTextBinding
import kndroidx.extension.compareTo

class StatusText(context: Context) : MiotBaseWidget<MiotWidgetStatusTextBinding>(context) {

    private val property by lazy { properties.first().second }

    override fun init() {
        binding.title <= property.description
    }

    override fun onValueChange(value: Any) {
        value as Number
        run loop@{
            property.valueList!!.forEach {
                if (it.value == value.toInt()) {
                    binding.title <= it.description
                    return@loop
                }
            }
        }
    }
}