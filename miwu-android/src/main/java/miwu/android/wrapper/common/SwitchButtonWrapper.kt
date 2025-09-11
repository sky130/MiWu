package miwu.android.wrapper.common

import android.content.Context
import miwu.android.R
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.wrapper.base.MiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.SwitchButton

@Wrapper(SwitchButton::class)
class SwitchButtonWrapper(context: Context, widget: MiwuWidget<Boolean>) :
    MiwuWrapper<Boolean>(context, widget) {

    private val binding by viewBinding(MiotWidgetListButtonBinding::inflate)
    private var value = false

    override val view get() = binding.root
    override val onClickView get() = binding.on

    override fun onUpdateValue(value: Boolean) {
        this.value = value
        if (value) {
            binding.on.setBackgroundResource(R.drawable.bg_item_blue)
        } else {
            binding.on.setBackgroundResource(R.drawable.bg_item)
        }
    }

    override fun initWrapper() {
        binding.desc.text = "开关"
    }

    override fun onClick() {
        value = !value
        update(value)
    }
}