package com.github.miwu.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton

@SuppressLint("UseCompatLoadingForDrawables")
class AppButton(context: Context, attr: AttributeSet) : AppCompatButton(context, attr) {

    init {
//        setBackgroundDrawable(
//            context.getDrawable(
//                R.drawable.bg_btn
//            )
//        )
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

    private fun animateToNormal() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 250L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 0.92f, 1.0f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 0.92f, 1.0f)
        val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 0.7f, 1.0f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }

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