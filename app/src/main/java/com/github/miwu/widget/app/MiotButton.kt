package com.github.miwu.widget.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.github.miwu.R

@SuppressLint("UseCompatLoadingForDrawables")
class MiotButton(context: Context, attr: AttributeSet) : AppCompatImageView(context, attr) {

    init {
        setBackgroundResource(R.drawable.bg_item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        if (isClickable) {
            if (paramMotionEvent.action != 0) {
                if (3 == paramMotionEvent.action || 1 == paramMotionEvent.action) animateToNormal()
                return super.onTouchEvent(paramMotionEvent)
            }
            animateToPress()
        }
        return super.onTouchEvent(paramMotionEvent)
    }

    @SuppressLint("Recycle")
    private fun animateToNormal() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 250L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 0.92f, 1.0f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 0.92f, 1.0f)
        val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 0.7f, 1.0f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }

    @SuppressLint("Recycle")
    private fun animateToPress() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.92f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.92f)
        val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.7f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }
}