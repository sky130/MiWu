package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiTextViewBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
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