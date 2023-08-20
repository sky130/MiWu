package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiSwitchCardBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
import kotlin.concurrent.thread


@SuppressLint("ClickableViewAccessibility")
class MiSwitchCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiSwitchCardBinding
    private var isChecked = false
    private val listener = ArrayList<(Boolean) -> Unit>()


    init {
        binding = MiSwitchCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiSwitchCard).apply {
            recycle()
        }
        setOnClickListener {
            setChecked(!isChecked)
        }
    }

    fun setOnStatusChangedListener(block: (Boolean) -> Unit) {
        listener.add(block)
    }

    fun setChecked(boolean: Boolean) {
        listener.forEach { it(boolean) }
        this.isChecked = boolean
        if (boolean) {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_on)
            binding.title.text = "点击开启"
        } else {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_off)
            binding.title.text = "点击关闭"
        }
    }

    fun getChecked(): Boolean {
        return isChecked
    }

}