package miwu.android.view.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AbsSeekBar
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

@SuppressLint("UseCompatLoadingForDrawables")
class AppSeekBar(
    context: Context,
    attr: AttributeSet,
) : BasicSeekbar(context, attr) {
    private val onSeekBarChangeListener by lazy {
        this::class.java
            .superclass
            .superclass
            .superclass
            .getDeclaredField("mOnSeekBarChangeListener")
            .apply { isAccessible = true }.get(this) as OnSeekBarChangeListener
    }
    var listener: ((Int) -> Unit)? = null

    init {
        thumb = null
        splitTrack = false
        isDuplicateParentStateEnabled = true
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }

    private var lastX = 0f
    private var lastProgress = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastProgress = progress
                parent.requestDisallowInterceptTouchEvent(true)
                onSeekBarChangeListener.onStartTrackingTouch(this)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastX
                val availableWidth = width - paddingLeft - paddingRight
                if (availableWidth <= 0) return true
                val progressPerPixel = max.toFloat() / availableWidth
                val deltaProgress = (dx * progressPerPixel).toInt()
                progress = lastProgress + deltaProgress
                onSeekBarChangeListener.onProgressChanged(this, progress, true)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                onSeekBarChangeListener.onStopTrackingTouch(this)
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    @SuppressLint("Recycle")
    private fun animateToNormal() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 250L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.1f, 1.0f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.1f, 1.0f)
        animatorSet.play(objectAnimator1).with(objectAnimator2)
        animatorSet.start()
    }

    @SuppressLint("Recycle")
    private fun animateToPress() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.1f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.1f)
        animatorSet.play(objectAnimator1).with(objectAnimator2)
        animatorSet.start()
    }
}