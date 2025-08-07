package miwu.android.wrapper

import android.content.Context
import miwu.android.databinding.MiotWidgetTextBinding
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.ValueText

@Wrapper(ValueText::class)
class ValueTextWrapper(context: Context, widget: MiwuWidget<Int>) :
    BaseMiwuWrapper<Int>(context, widget) {

    private val binding by viewBinding(MiotWidgetTextBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: Int) {
        binding.value.text = valueList.firstOrNull { it.value == value }?.descriptionTranslation ?: "--"
    }

    override fun initWrapper() {
        binding.unit.text = valueUnit
        binding.desc.text = descriptionTranslation
    }
}