package miwu.android.wrapper.vacuum

import android.content.Context
import android.view.View
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.view.binding.updateState
import miwu.android.wrapper.base.MiwuActionWrapper
import miwu.annotation.Wrapper
import miwu.spec.MiotSpec.Action
import miwu.spec.MiotSpec.Property
import miwu.spec.MiotSpec.Service
import miwu.support.MiwuWidget
import miwu.widget.VacuumButton

@Wrapper(VacuumButton::class)
class VacuumButtonWrapper(context: Context, widget: MiwuWidget<Unit>) :
    MiwuActionWrapper(context, widget) {
    private val binding by viewBinding(MiotWidgetListButtonBinding::inflate)
    private val status by lazy { getProperty(Service.Vacuum, Property.Mode) }
    override val view: View get() = binding.root
    override val onClickView: View get() = binding.on

    override fun initWrapper() {
        binding.on.setIcon(icon)
        binding.desc.text = descriptionTranslation
        registerProperty(Service.Vacuum, Property.Mode) { _, value ->
            if (value !is Int) return@registerProperty
            val list = status?.valueList ?: return@registerProperty
            val desc =
                list.firstOrNull { it.value == value }?.description ?: return@registerProperty
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
        binding.updateState(true)
    }

    private fun disable() {
        binding.updateState(false)
    }

}