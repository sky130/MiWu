package com.github.miwu.widget.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.miwu.R

@SuppressLint("UseCompatLoadingForDrawables")
class AppSeekBar(
    context: Context,
    attr: AttributeSet,
) : AppCompatSeekBar(context, attr) {
    private var minProgress: Int = 0
    private var maxProgress: Int = 100
    var listener: ((Int) -> Unit)? = null
    var isOnTouching = false

    init {
        thumb = null
        splitTrack = false
        isDuplicateParentStateEnabled = true
    }



    private var x1 = 0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                isOnTouching = false
            }
            MotionEvent.ACTION_DOWN->{
                x1 = event.x
            }
            MotionEvent.ACTION_MOVE->{
                val x2 = event.x
                if(x1 - x2 > 50 || x2 - x1 > 50) {
                    parent.requestDisallowInterceptTouchEvent(true) // 解决手势冲突问题
                }
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

    fun getCurrentProgress(): Int {
        return getCurrentProgress(progress)
    }

    fun setCurrentProgress(progress: Int) {
        this.progress = getRelativeProgress(progress)
    }

    private fun getCurrentProgress(progress: Int): Int {
        val currentProgress =
            (progress.toFloat() / 100f) * (maxProgress - minProgress) + minProgress
        return currentProgress.toInt()
    }

    private fun getRelativeProgress(progress: Int): Int {
        val currentProgress =
            (progress.toFloat() - minProgress) / (maxProgress - minProgress) * 100f
        return currentProgress.toInt()
    }

    fun setMaxProgress(max: Int) {
        this.maxProgress = max
    }

    fun setMinProgress(min: Int) {
        this.minProgress = min
    }

}