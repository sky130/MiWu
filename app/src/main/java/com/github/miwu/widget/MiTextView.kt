package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.miwu.R
import com.github.miwu.databinding.MiTextViewBinding
import com.github.miwu.logic.network.DeviceService
import com.github.miwu.ui.DeviceActivity
import kotlin.concurrent.thread

class MiTextView(context: Context, attr: AttributeSet) : LinearLayout(context, attr), ViewExtra {

    private var binding: MiTextViewBinding
    private val unit: String
    private var block: ((Int) -> Unit)? = null

    init {
        binding = MiTextViewBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiTextView).apply {
            binding.title.text = getString(R.styleable.MiTextView_title).toString()
            unit = getString(R.styleable.MiTextView_unit).toString()
            setText("0")
            recycle()
        }
    }


    @SuppressLint("SetTextI18n")
    fun setText(text: CharSequence) {
        binding.value.text = "$text$unit"
    }
}