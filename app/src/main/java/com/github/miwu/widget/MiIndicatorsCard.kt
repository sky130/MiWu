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
    private var min = 0
    private var max = 10

    //    private var block: (Int) -> Unit = {}
    private val unit: String

    init {
        binding = MiIndicatorsCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiIndicatorsCard).apply {
            unit = getString(R.styleable.MiIndicatorsCard_unit).toString()
            setProgress(0)
            setTitle(getString(R.styleable.MiIndicatorsCard_title).toString())
            binding.seekbar.setDotMode(getInt(R.styleable.MiIndicatorsCard_dotMode, 0))
            min = getInt(R.styleable.MiIndicatorsCard_minProgress, 0)
            max = getInt(R.styleable.MiIndicatorsCard_maxProgress, 10)
            setProgressMax(max - min)
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

    fun setProgress(progress: Int) {
        if (progress > max || progress < min) return
        setProgress(progress, true)
        listeners.forEach { it(progress) }
    }

    fun setProgress(max: Int, min: Int) {
        this.max = max
        this.min = min
        setProgressMax(max - min)
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int, boolean: Boolean) {
        binding.seekbar.setIndex(progress - min)
        binding.value.text = "$progress$unit"
    }

    fun getProgress() = binding.seekbar.getIndex() + min


    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setOnProgressChangerListener(block: (Int) -> Unit) {
        listeners.add(block)
    }

}