package miwu.android.wrapper.light

import android.content.Context
import android.widget.SeekBar
import miwu.android.databinding.MiotWidgetColorTemperatureSeekbarBinding
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.ColorTemperatureSeekbar

@Wrapper(ColorTemperatureSeekbar::class)
class ColorTemperatureSeekbarWrapper(context: Context, widget: MiwuWidget<Int>) :
    BaseMiwuWrapper<Int>(context, widget), SeekBar.OnSeekBarChangeListener {

    private val binding by viewBinding(MiotWidgetColorTemperatureSeekbarBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: Int) {
        binding.seekbar.progress = value
    }

    override fun initWrapper(){
        binding.seekbar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(
        seekBar: SeekBar,
        progress: Int,
        fromUser: Boolean
    ) {
        if (!fromUser) return
        update(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

}