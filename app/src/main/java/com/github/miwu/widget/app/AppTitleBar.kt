package com.github.miwu.widget.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.github.miwu.R
import com.github.miwu.databinding.AppTitleBarBinding
import kndroidx.extension.compareTo

class AppTitleBar(context: Context, attr: AttributeSet) : LinearLayout(context, attr),
    OnClickListener {
    private var binding: AppTitleBarBinding
    private val back: Boolean
    private val title: String
    private var mOnClickListener: OnClickListener? = null

    init {
        binding = AppTitleBarBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.AppTitleBar, 0, 0).apply {
            back = getBoolean(R.styleable.AppTitleBar_back, false)
            title = getString(R.styleable.AppTitleBar_title) ?: "null"
        }.recycle()
        setTitle(title)
        binding.icon?.visibility = if (back) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.titleArea?.setOnClickListener(this)
    }

    fun setTitle(title: String) {
        binding.title <= title
    }

    override fun onClick(p0: View) {
        if (mOnClickListener == null) {
            try {
                (context as Activity).finish()
            } catch (_: Exception) {
            }
        } else {
            mOnClickListener?.onClick(this)
        }
    }

    fun setTitleOnClick(listener: OnClickListener) {
        this.mOnClickListener = listener
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
        val objectAnimator1 = ObjectAnimator.ofFloat(binding.titleArea, "scaleX", 0.92f, 1.0f)
        val objectAnimator2 = ObjectAnimator.ofFloat(binding.titleArea, "scaleY", 0.92f, 1.0f)
        val objectAnimator3 = ObjectAnimator.ofFloat(binding.titleArea, "alpha", 0.7f, 1.0f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }

    @SuppressLint("Recycle")
    private fun animateToPress() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200L
        val objectAnimator1 = ObjectAnimator.ofFloat(binding.titleArea, "scaleX", 1.0f, 0.92f)
        val objectAnimator2 = ObjectAnimator.ofFloat(binding.titleArea, "scaleY", 1.0f, 0.92f)
        val objectAnimator3 = ObjectAnimator.ofFloat(binding.titleArea, "alpha", 1.0f, 0.7f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }


}