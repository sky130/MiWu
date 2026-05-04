package miwu.android.wrapper.common

import android.content.Context
import androidx.core.view.isVisible
import miwu.android.R
import miwu.android.databinding.MiotWidgetSwitchBarBinding
import miwu.android.wrapper.base.MiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.MiwuWidget
import miwu.widget.SwitchBar

@Wrapper(SwitchBar::class)
class SwitchBarWrapper(context: Context, widget: MiwuWidget<Boolean>) :
    MiwuWrapper<Boolean>(context, widget) {
    override val view get() = binding.root
    private val binding by viewBinding(MiotWidgetSwitchBarBinding::inflate)
    private var value = false
        set(value) {
            field = value
            val (res, text) = when (value) {
                true -> R.drawable.bg_switch_button_on to "关闭"
                false -> R.drawable.bg_switch_button_off to "开启"
            }
            binding.apply {
                img.setBackgroundResource(res)
                title.text = text
            }
        }

    override fun onUpdateValue(value: Boolean) {
        this.value = value
    }

    override fun initWrapper() {
        if (deviceType != "light") {
            with(binding.subTitle) {
                text = descriptionTranslation
                isVisible = true
            }
        }
        binding.img.setIcon(icon)
    }

    override fun onClick() {
        value = !value
        update(value)
    }
}