package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiRoundSeekBarCardBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
import kotlin.concurrent.thread


@SuppressLint("ClickableViewAccessibility")
class MiRoundSeekBarCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiRoundSeekBarCardBinding
    private val unit: String
    private var block: ((Int) -> Unit)? = null
    private lateinit var did: String


    fun setOnProgressChangedListener(block: (Int) -> Unit) {
        this.block = block
    }

    init {
        binding = MiRoundSeekBarCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiRoundSeekBarCard).apply {
            binding.seekbar.setMaxProgress(getInt(R.styleable.MiRoundSeekBarCard_maxProgress, 100))
            binding.seekbar.setMinProgress(getInt(R.styleable.MiRoundSeekBarCard_minProgress, 0))
            val colors = intArrayOf(
                getColor(
                    R.styleable.MiRoundSeekBarCard_progressBarColorStart,
                    ContextCompat.getColor(context, R.color.color_temperature_start)
                ), // 开始时的颜色
                getColor(
                    R.styleable.MiRoundSeekBarCard_progressBarColorEnd,
                    ContextCompat.getColor(context, R.color.color_temperature_end)
                ), // 结束时的颜色
            )
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, // 渐变方向（从左到右）
                colors // 颜色数组
            )
            gradientDrawable.shape = GradientDrawable.OVAL
            gradientDrawable.cornerRadius = 0f // 圆角半径
            binding.seekbar.background = gradientDrawable
            binding.title.text = getString(R.styleable.MiRoundSeekBarCard_title).toString()
            unit = getString(R.styleable.MiRoundSeekBarCard_unit).toString()
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
        binding.seekbar.progress = progress.toInt()
        binding.value.text = "${progress.toInt()}$unit"
    }

}