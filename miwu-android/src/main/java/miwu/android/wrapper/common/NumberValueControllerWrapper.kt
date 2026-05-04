package miwu.android.wrapper.common

import android.content.Context
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.MiwuWidget
import miwu.support.unit.ValueUnit
import miwu.widget.NumberValueController
import miwu.android.databinding.MiotWidgetIntValueControllerBinding as Binding

@Wrapper(NumberValueController::class)
class NumberValueControllerWrapper(context: Context, widget: MiwuWidget<Number>) :
    ViewMiwuWrapper<Number>(context, widget) {

    private val binding by viewBinding(Binding::inflate)
    private val _valueStep = widget.valueStep.toDouble()
    private val valurRangeFrom = widget.valueRange.from.toDouble()
    private val valurRangeTo = widget.valueRange.to.toDouble()

    private var value: Double = 0.0
        set(value) {
            field = value
            binding.num.text = value.toString().removeSuffix(".0")
        }

    override val view get() = binding.root

    override fun onUpdateValue(value: Number) {
        this.value = value.toDouble()
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
            unit.text = when (valueOriginUnit) {
                ValueUnit.Celsius -> "°"
                else -> valueUnit
            }
        }
    }

    fun Double.plus() = (this + _valueStep).coerceIn(valurRangeFrom, valurRangeTo)

    fun Double.subtract() = (this - _valueStep).coerceIn(valurRangeFrom, valurRangeTo)

}