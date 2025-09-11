package miwu.android.wrapper.vacuum

import android.content.Context
import android.view.View
import miwu.android.R
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.wrapper.base.MiwuActionWrapper
import miwu.annotation.Wrapper
import miwu.spec.Action
import miwu.spec.Property
import miwu.spec.Service
import miwu.support.base.MiwuWidget
import miwu.widget.VacuumButton

@Wrapper(VacuumButton::class)
class VacuumButtonWrapper(context: Context, widget: MiwuWidget<Unit>) :
    MiwuActionWrapper(context, widget) {
    private val binding by viewBinding(MiotWidgetListButtonBinding::inflate)
    private val status by lazy { getProperty(Service.Vacuum, Property.Mode) }
    override val view: View get() = binding.root
    override val onClickView: View get() = binding.on

    override fun initWrapper() {
        AndroidIcon {
            binding.on.setImageResource(resId)
        }
        binding.desc.text = descriptionTranslation
        register(Service.Vacuum, Property.Mode) { value ->
            if (value !is Int) return@register
            val list = status?.valueList ?: return@register
            val desc = list.firstOrNull { it.value == value }?.description ?: return@register
            when (desc) {
                "Sweeping", "Sweeping and Mopping", "Mopping" -> onCleaning()
                "Charging", "Go Charging" -> onCharging()
                "Upgrading" -> Unit
                else -> disable()
            }
        }
    }

    override fun onClick() {
        action()
    }

    private fun onCleaning() {
        when (actionName) {
            Action.StartCharge -> disable()
            Action.StopCharge -> enabled()
        }
    }

    private fun onCharging() {
        when (actionName) {
            Action.StopCharge -> enabled()
            Action.StartCharge -> disable()
        }
    }

    private fun enabled() {
        binding.on.setBackgroundResource(R.drawable.bg_item_blue)
    }

    private fun disable() {
        binding.on.setBackgroundResource(R.drawable.bg_item)
    }

}