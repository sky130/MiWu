package com.github.miwu.widget.miot

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.github.miwu.databinding.MiotWidgetSwitchBinding


@SuppressLint("ViewConstructor")
class Switch @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
    override val siid: Int,
    override val piid: Int,
    override val putValue: (Any, Int, Int) -> Unit,
) : LinearLayout(context, attributeSet, defStyleAttr), MiotWidgetImpl<MiotWidgetSwitchBinding> {

    override var binding = createViewBinding()

    override fun onValueChange(value: Any) {

    }

}