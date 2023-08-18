package io.github.sky130.miwu.widget

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
import android.widget.LinearLayout
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiTitleViewBinding

class MiTitleView(context: Context, attr: AttributeSet) : LinearLayout(context, attr),
    OnClickListener {
    private var binding: MiTitleViewBinding
    private lateinit var mActivity: Activity
    private var onClick: (() -> Unit)? = null

    init {
        val attrArray = context.obtainStyledAttributes(attr, R.styleable.MiTitleView)
        val text = attrArray.getString(R.styleable.MiTitleView_title).toString()
        attrArray.recycle()
        binding = MiTitleViewBinding.inflate(LayoutInflater.from(context), this, true)
        binding.title.text = text
        setOnClickListener(this)
    }

    fun setBack(boolean: Boolean) {
        if (boolean) {
            binding.titleImage.visibility = VISIBLE
        } else {
            binding.titleImage.visibility = GONE
        }
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setActivity(activity: Activity) {
        mActivity = activity
    }

    override fun onClick(p0: View?) {
        if (binding.titleImage.visibility == VISIBLE) {
            if (onClick == null) {
                mActivity.finish()
            } else {
                onClick!!()
            }
        }
    }

    fun setOnClick(block: () -> Unit) {
        onClick = block
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