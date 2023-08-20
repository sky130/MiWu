package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiSeekBarCardBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
import kotlin.concurrent.thread

class MiSeekBarCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiSeekBarCardBinding
    private val unit: String
    private var block: ((Int) -> Unit)? = null


    fun setOnProgressChangedListener(block: (Int) -> Unit) {
        this.block = block
    }

    init {
        binding = MiSeekBarCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiSeekBarCard).apply {
            binding.seekbar.setMaxProgress(getInt(R.styleable.MiSeekBarCard_maxProgress, 100))
            binding.seekbar.setMinProgress(getInt(R.styleable.MiSeekBarCard_minProgress, 0))

            binding.seekbar.setProgressBackgroundColor(
                getColor(
                    R.styleable.MiSeekBarCard_progressBarColor,
                    ContextCompat.getColor(context, R.color.white)
                )
            )

            binding.title.text = getString(R.styleable.MiSeekBarCard_title).toString()
            unit = getString(R.styleable.MiSeekBarCard_unit).toString()
            recycle()
        }
        binding.seekbar.setOnProgressChanged(false) {
            setCurrentProgress(it)
        }
        binding.seekbar.setOnProgressChanged(true) {
            setCurrentProgress(it)
            block?.invoke(it)
        }
    }

    fun getSeekBar() = binding.seekbar

    @SuppressLint("SetTextI18n")
    fun setCurrentProgress(progress: Number) {
        if (isFocusableInTouchMode) return
        binding.seekbar.progress = progress.toInt()
        binding.value.text = "${progress.toInt()}$unit"
    }

}