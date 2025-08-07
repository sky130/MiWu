package miwu.android.wrapper

import android.content.Context
import miwu.android.databinding.MiotWidgetIntValueControllerBinding
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.support.unit.Unit
import miwu.widget.DoubleValueController

@Wrapper(DoubleValueController::class)
class DoubleValueControllerWrapper(context: Context, widget: MiwuWidget<Double>) :
    BaseMiwuWrapper<Double>(context, widget) {

    private val binding by viewBinding<MiotWidgetIntValueControllerBinding>()
    private var value = 0.0
        get() = field
        set(value) {
            binding.num.text =
                if (value.toString().endsWith(".0")) {
                    value.toInt()
                } else {
                    value
                }.toString()
            field = value
        }

    override val view get() = binding.root

    override fun onUpdateValue(value: Double) {
        this.value = value
    }

    override fun initWrapper() {
        binding.apply {
            down.setOnClickListener {
                value = value.subtract()
                update(value)
            }
            up.setOnClickListener {
                value = value.plus()
                update(value)
            }
            unit.text =
                when (valueOriginUnit) {
                    Unit.Celsius -> "°"
                    else -> valueUnit
                }
        }
    }

    fun Double.plus() = (this + valueStep).coerceIn(valueRange.from, valueRange.to)

    fun Double.subtract() = (this - valueStep).coerceIn(valueRange.from, valueRange.to)

}