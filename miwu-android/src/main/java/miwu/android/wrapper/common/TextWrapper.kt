package miwu.android.wrapper.common

import android.content.Context
import miwu.android.databinding.MiotWidgetTextBinding
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.Text

@Wrapper(Text::class)
class TextWrapper(context: Context, widget: MiwuWidget<String>) :
    ViewMiwuWrapper<String>(context, widget) {

    private val binding by viewBinding(MiotWidgetTextBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: String) {
        binding.value.text = value
    }

    override fun initWrapper() {
        binding.unit.text = valueUnit
        binding.desc.text = descriptionTranslation
    }
}