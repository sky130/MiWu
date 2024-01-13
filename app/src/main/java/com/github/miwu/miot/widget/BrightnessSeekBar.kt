package com.github.miwu.miot.widget

import android.content.Context
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.github.miwu.databinding.MiotWidgetBrightnessSeekbarBinding as Binding

class BrightnessSeekBar(context: Context) : MiotBaseWidget<Binding>(context),
    OnSeekBarChangeListener {
    init {
        binding.seekbar.setOnSeekBarChangeListener(this)
    }

    override fun onValueChange(value: Any) {
        value as Number
        binding.seekbar.progress = value.toInt()
    }

    override fun onProgressChanged(p0: SeekBar, p1: Int, fromUser: Boolean) = Unit

    override fun onStartTrackingTouch(p0: SeekBar) {
        stopRefresh()
    }

    override fun onStopTrackingTouch(p0: SeekBar) {
        putValue(p0.progress)
        startRefresh()
    }
}