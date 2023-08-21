package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.miwu.R
import com.github.miwu.databinding.MiSwitchCardBinding


@SuppressLint("ClickableViewAccessibility")
class MiSwitchCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiSwitchCardBinding
    private var isChecked = false
    private val listener = ArrayList<(Boolean) -> Unit>()
    private val mustListener = ArrayList<(Boolean) -> Unit>()


    init {
        binding = MiSwitchCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiSwitchCard).apply {
            recycle()
        }
        setOnClickListener {
            setChecked(!isChecked)
        }
    }

    fun setOnStatusChangedListener(isMust: Boolean = false,block: (Boolean) -> Unit) {
        if (isMust) {
            mustListener.add(block)
        } else {
            listener.add(block)
        }
    }

    fun setChecked(boolean: Boolean, isListener: Boolean = true) {
        if (isListener)
            listener.forEach { it(boolean) }
        mustListener.forEach { it(boolean) }
        this.isChecked = boolean
        if (!boolean) {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_on)
            binding.title.text = "点击开启"
        } else {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_off)
            binding.title.text = "点击关闭"
        }
    }
}