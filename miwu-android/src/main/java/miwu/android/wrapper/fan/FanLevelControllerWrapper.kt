package miwu.android.wrapper.fan

import android.content.Context
import miwu.android.databinding.MiotWidgetFanLevelBinding
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.FanLevelController

@Wrapper(FanLevelController::class)
class FanLevelControllerWrapper(context: Context, widget: MiwuWidget<Int>) :
    BaseMiwuWrapper<Int>(context, widget) {
    private val binding by viewBinding(MiotWidgetFanLevelBinding::inflate)
    override val view get() = binding.root
    private var max: Int = 0
        set(value) {
            field = value
            binding.numLarge.text = value.toString()
        }
    private var min: Int = 0
    private var value = 0
        set(value) {
            if (value == field) return
            binding.num.text = value.toString()
            field = value
            update(value)
        }

    override fun initWrapper() {
        val first = valueList.first().value
        val last = valueList.last().value
        max = maxOf(first, last)
        min = minOf(first, last)
        binding.num.text = "-"
        with(binding) {
            fun updateValue(delta: Int) {
                value = (value + delta).coerceIn(min, max)
            }
            plus.onClick { updateValue(1) }
            minus.onClick { updateValue(-1) }
        }
    }

    override fun onUpdateValue(value: Int) {
        this.value = value
    }

}