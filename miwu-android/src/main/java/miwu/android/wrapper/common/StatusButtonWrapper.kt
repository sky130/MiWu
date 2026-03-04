package miwu.android.wrapper.common

import android.content.Context
import miwu.android.R
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.wrapper.base.MiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.ModeButton
import miwu.widget.StatusButton

@Wrapper(StatusButton::class)
class StatusButtonWrapper(context: Context, widget: MiwuWidget<Int>) :
    MiwuWrapper<Int>(context, widget) {

    private val binding by viewBinding(MiotWidgetListButtonBinding::inflate)
    private var currentValue: Int = -1
    override val view get() = binding.root
    override val onClickView get() = binding.on

    override fun onUpdateValue(value: Int) {
        currentValue = value
        if (value == defaultValue) {
            binding.on.setBackgroundResource(R.drawable.bg_item_blue)
        } else {
            binding.on.setBackgroundResource(R.drawable.bg_item)
        }
    }

    override fun initWrapper() {
        binding.on.setIcon(icon)
        binding.desc.text = descriptionTranslation
    }

    override fun onClick() {
        if (currentValue == defaultValue) {
            update(specialValue)
        } else {
            update(defaultValue)
        }
    }
}