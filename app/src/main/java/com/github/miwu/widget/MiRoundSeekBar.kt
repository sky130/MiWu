package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.miwu.R
import com.github.miwu.util.ViewUtils.toPx


@SuppressLint("UseCompatLoadingForDrawables")
class MiRoundSeekBar(
    context: Context,
    attr: AttributeSet,
) : AppCompatSeekBar(context, attr), OnSeekBarChangeListener {
    private var listener1: ((Int) -> Unit)? = null
    private var listener2: ((Int) -> Unit)? = null
    private var minProgress: Int = 0
    private var maxProgress: Int = 100

    init {
        thumb = context.getDrawable(R.drawable.mi_seekbar_thumb)
        thumbOffset = (-3f).toPx(context)
        splitTrack = false
        isDuplicateParentStateEnabled = true
        progressDrawable = context.getDrawable(R.drawable.mi_round_seek_seekbar)
        this.progress = progress
        setOnSeekBarChangeListener(this)
    }

    private fun isTouchInThumb(event: MotionEvent, thumbBounds: Rect): Boolean {
        val x = event.x
        val y = event.y
        //根据偏移量和左边距确定thumb位置
        val left = thumbBounds.left - thumbOffset + paddingLeft
        val right = left + thumbBounds.width()
        return x >= left && x <= right && y >= thumbBounds.top && y <= thumbBounds.bottom
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