package com.github.miwu.miot.widget

import android.content.Context
import com.github.miwu.databinding.MiotWidgetStatusTextBinding
import kndroidx.extension.compareTo

class StatusText(context: Context) : MiotBaseWidget<MiotWidgetStatusTextBinding>(context) {
    override fun onValueChange(value: Any) {
        value as Double
        run loop@{
            properties[0].second.valueList!!.forEach {
                if (it.value == value.toInt()) {
                    binding.title <= it.description
                    return@loop
                }
            }
        }
    }
}