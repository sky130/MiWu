package miwu.android.view.binding

import miwu.android.R
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.wrapper.base.ViewMiwuWrapper.Companion.setIcon
import miwu.support.MiwuWrapper
import miwu.support.icon.Icon

@MiwuWrapper.WrapperFun
fun MiotWidgetListButtonBinding.icon(
    icon: Icon? = null,
) {
    icon?.let { on.setIcon(it) }
}

@MiwuWrapper.WrapperFun
var MiotWidgetListButtonBinding.title: String
    get() = desc.text.toString()
    set(value) {
        desc.text = value
    }

@MiwuWrapper.WrapperFun
inline fun MiotWidgetListButtonBinding.onClick(
    crossinline onClick: () -> Unit = {}
) {
    on.setOnClickListener { onClick() }
}

fun MiotWidgetListButtonBinding.updateState(enabled: Boolean) {
    on.setBackgroundResource(if (enabled) R.drawable.bg_item_blue else R.drawable.bg_item)
}