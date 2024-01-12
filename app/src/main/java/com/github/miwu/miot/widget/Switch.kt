package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import com.github.miwu.databinding.MiotWidgetSwitchBinding


class Switch @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
) : MiotBaseWidget<MiotWidgetSwitchBinding>(context, attributeSet, defStyleAttr) {

    override fun onValueChange(value: Any) {

    }

}