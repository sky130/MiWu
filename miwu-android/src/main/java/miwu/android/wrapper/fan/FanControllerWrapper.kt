package miwu.android.wrapper.fan

import android.content.Context
import android.widget.SeekBar
import miwu.android.databinding.MiotWidgetSeekbarBinding
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.FanController

@Wrapper(FanController::class)
class FanControllerWrapper(context: Context, widget: MiwuWidget<Int>) :
    ViewMiwuWrapper<Int>(context, widget), SeekBar.OnSeekBarChangeListener {

    private val binding by viewBinding(MiotWidgetSeekbarBinding::inflate)
    override val view get() = binding.root

    override fun onUpdateValue(value: Int) {
        binding.seekbar.progress = value
    }

    override fun initWrapper() {
        binding.icon.setIcon(icon)
        binding.seekbar.setOnSeekBarChangeListener(this)
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
        update(binding.seekbar.getCurrentProgress())
        continueUpdate()
    }

}