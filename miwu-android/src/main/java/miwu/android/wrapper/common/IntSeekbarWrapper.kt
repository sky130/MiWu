package miwu.android.wrapper.common

import android.content.Context
import android.widget.SeekBar
import miwu.android.databinding.MiotWidgetSeekbarBinding
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.IntSeekbar

@Wrapper(IntSeekbar::class)
class IntSeekbarWrapper(context: Context, widget: MiwuWidget<Int>) :
    ViewMiwuWrapper<Int>(context, widget), SeekBar.OnSeekBarChangeListener {

    private val binding by viewBinding(MiotWidgetSeekbarBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: Int) {
        binding.seekbar.progress = value
    }

    override fun initWrapper() {
        binding.icon.setIcon(icon)
        binding.seekbar.setOnSeekBarChangeListener(this)
        binding.seekbar.max = valueRange.to
        binding.seekbar.min = valueRange.from
    }

    override fun onProgressChanged(
        seekBar: SeekBar,
        progress: Int,
        fromUser: Boolean
    ) = Unit

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        stopUpdate()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        update(binding.seekbar.progress)
        continueUpdate()
    }

}