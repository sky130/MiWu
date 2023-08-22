package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.miwu.R
import com.github.miwu.databinding.MiIndicatorsCardBinding
import com.github.miwu.databinding.MiRoundSeekBarCardBinding


class MiIndicatorsCard(
    context: Context,
    attr: AttributeSet,
) : ConstraintLayout(context, attr) {

    private var binding: MiIndicatorsCardBinding
    private val listeners = ArrayList<(Int) -> Unit>()
//    private var block: (Int) -> Unit = {}
    private val unit: String

    init {
        binding = MiIndicatorsCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiIndicatorsCard).apply {
            unit = getString(R.styleable.MiIndicatorsCard_unit).toString()
            setProgress(0)
            setTitle(getString(R.styleable.MiIndicatorsCard_title).toString())
            recycle()
        }
        binding.add.setOnClickListener {
            setProgress(getProgress() + 1)
        }

        binding.subtraction.setOnClickListener {
            setProgress(getProgress() - 1)
        }
    }

    fun setProgressMax(size: Int) {
        binding.seekbar.setDotSize(size)
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        listeners.forEach { it(progress) }
        binding.seekbar.setIndex(progress)
        binding.value.text = "$progress$unit"
    }

    fun setProgress(progress: Int,boolean: Boolean){
        binding.seekbar.setIndex(progress)
        binding.value.text = "$progress$unit"
    }

    fun getProgress() = binding.seekbar.getIndex()


    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setOnProgressChangerListener(block: (Int) -> Unit) {
        listeners.add(block)
    }

}