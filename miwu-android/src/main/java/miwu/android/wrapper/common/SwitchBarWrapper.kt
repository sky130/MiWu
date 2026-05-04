package miwu.android.wrapper.common

import android.content.Context
import android.view.View
import miwu.android.R
import miwu.android.databinding.MiotWidgetSwitchBarBinding
import miwu.android.wrapper.base.MiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.MiwuWidget
import miwu.widget.SwitchBar

@Wrapper(SwitchBar::class)
class SwitchBarWrapper(context: Context, widget: MiwuWidget<Boolean>) :
    MiwuWrapper<Boolean>(context, widget) {

    private var value = false
    private val binding by viewBinding(MiotWidgetSwitchBarBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: Boolean) {
        this.value = value
        binding.apply {
            if (value) {
                img.setBackgroundResource(R.drawable.bg_switch_button_on)
                title.text = "关闭"
            } else {
                img.setBackgroundResource(R.drawable.bg_switch_button_off)
                title.text = "开启"
            }
        }
    }

    override fun initWrapper() {
        if (serviceName != "light") {
            binding.subTitle.text = translateHelper.translate(serviceName)
            binding.subTitle.visibility = View.VISIBLE
        }
        binding.img.setIcon(icon)
    }

    override fun onClick() {
        value = !value
        update(value)
    }

}