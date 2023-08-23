package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.miwu.R
import com.github.miwu.databinding.MiButtonCardBinding
import com.github.miwu.databinding.MiSwitchCardBinding


@SuppressLint("ClickableViewAccessibility")
class MiButtonCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiButtonCardBinding
    private var isChecked = false
    private val listener = ArrayList<(Boolean) -> Unit>()
    private val mustListener = ArrayList<(Boolean) -> Unit>()


    init {
        binding = MiButtonCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiButtonCard).apply {
            binding.miSwitchButton.setImageResource(
                getResourceId(
                    R.styleable.MiButtonCard_srcIcon,
                    R.drawable.ic_pause_round
                )
            )
            binding.title.text = getString(R.styleable.MiButtonCard_title).toString()
            recycle()
        }
    }

}