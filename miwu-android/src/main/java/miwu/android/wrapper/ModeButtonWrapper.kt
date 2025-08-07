package miwu.android.wrapper

import android.content.Context
import miwu.android.R
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.wrapper.base.MiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import kotlin.getValue
import miwu.widget.ModeButton


@Wrapper(ModeButton::class)
class ModeButtonWrapper(context: Context, widget: MiwuWidget<Int>) : MiwuWrapper<Int>(context, widget) {

    private val binding by viewBinding(MiotWidgetListButtonBinding::inflate)
    override val view get() = binding.root
    override val onClickView get() = binding.on

    override fun onUpdateValue(value: Int) {
        if (value == defaultValue) {
            binding.on.setBackgroundResource(R.drawable.bg_item_blue)
        } else {
            binding.on.setBackgroundResource(R.drawable.bg_item)
        }
    }

    override fun initWrapper() {
        AndroidIcon {
            binding.on.setImageResource(resId)
        }
        binding.desc.text = descriptionTranslation
    }

    override fun onClick() {
        update(defaultValue)
    }
}