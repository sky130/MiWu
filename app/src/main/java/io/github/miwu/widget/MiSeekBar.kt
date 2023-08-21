package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import io.github.sky130.miwu.R

@SuppressLint("UseCompatLoadingForDrawables")
class MiSeekBar(
    context: Context,
    attr: AttributeSet,
) : AppCompatSeekBar(context, attr), OnSeekBarChangeListener {
    private var listener1: ((Int) -> Unit)? = null
    private var listener2: ((Int) -> Unit)? = null
    private var minProgress: Int = 0
    private var maxProgress: Int = 100


    init {
        thumb = null
        splitTrack = false
        isDuplicateParentStateEnabled = true
        progressDrawable = context.getDrawable(R.drawable.mi_seek_seekbar)
        this.progress = progress
        setOnSeekBarChangeListener(this)
    }

    fun setProgressBackgroundColor(color: Int) {
        progressTintList = ColorStateList.valueOf(color)
    }

    fun setMaxProgress(max: Int) {
        this.maxProgress = max
    }

    fun setMinProgress(min: Int) {
        this.minProgress = min
    }

    override fun setProgress(progress: Int) {
        super.setProgress(getRelativeProgress(progress))
    }

    //是否直到结束才回调
    fun setOnProgressChanged(isFromFinish: Boolean, block: ((Int) -> Unit)) {
        if (isFromFinish) {
            this.listener2 = block
        } else {
            this.listener1 = block
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true) // 解决手势冲突问题
        return super.onTouchEvent(event)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (!p2) return
        listener1?.let { it(getCurrentProgress(p1)) }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {
        listener2?.let { it(getCurrentProgress()) }
    }

    fun getCurrentProgress(): Int {
        return getCurrentProgress(progress)
    }

    private fun getCurrentProgress(progress: Int): Int {
        val currentProgress =
            (progress.toFloat() / 100f) * (maxProgress - minProgress) + minProgress
        return currentProgress.toInt()
    }

    private fun getRelativeProgress(progress: Int): Int {
        val currentProgress = (progress.toFloat() - minProgress) / (maxProgress - minProgress) * 100f
        return currentProgress.toInt()
    }


}