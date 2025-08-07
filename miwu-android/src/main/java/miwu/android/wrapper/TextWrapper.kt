package miwu.android.wrapper

import android.content.Context
import miwu.android.databinding.MiotWidgetTextBinding
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.Text

@Wrapper(Text::class)
class TextWrapper(context: Context, widget: MiwuWidget<String>) :
    BaseMiwuWrapper<String>(context, widget) {

    private val binding by viewBinding<MiotWidgetTextBinding>()
    override val view get() = binding.root

    override fun onUpdateValue(value: String) {
        binding.value.text = value
    }

    override fun initWrapper() {
        binding.unit.text = valueUnit
        binding.desc.text = descriptionTranslation
    }
}