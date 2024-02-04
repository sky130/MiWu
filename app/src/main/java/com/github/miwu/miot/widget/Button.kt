package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetButtonBinding
import com.github.miwu.databinding.MiotWidgetSwitchBinding
import kndroidx.extension.compareTo


class Button(context: Context) : MiotBaseWidget<MiotWidgetButtonBinding>(context) {
    private val action get() = actions.first()

    override fun init() {
        setOnClickListener {
            doAction(action.first, action.second.iid)
        }
    }


    override fun onValueChange(value: Any) = Unit

}