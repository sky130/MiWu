package com.github.miwu.widget.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import com.github.miwu.R
import com.github.miwu.databinding.BackTitleBarBinding

@SuppressLint("CustomViewStyleable")
class BackTitleBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
) :
    LinearLayout(context, attributeSet, defStyleAttr) {
    private var backIcon: ImageView? = null
    private var leftArea: LinearLayout? = null
    private var textClock: TextClock? = null
    private var titleTextView: TextView? = null
    private var titleText: String?
    private var isAnimating = false

    init {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.BackTitleBar, 0, 0)
        titleText = typedArray.getString(R.styleable.BackTitleBar_title)
        typedArray.recycle()
        this.initializeViews(context)
    }

    private fun initializeViews(context: Context) {
        BackTitleBarBinding.inflate(LayoutInflater.from(context), this, true)
        isClickable = false
//        inflate(context, R.layout.back_title_bar, this)
        leftArea = findViewById(R.id.left_area)
        backIcon = findViewById(R.id.left_icon)
        textClock = findViewById(R.id.clock_stub)
        titleTextView = findViewById(R.id.title_textview)
        titleTextView?.text = titleText
        titleTextView?.isSelected = true
        titleTextView?.marqueeRepeatLimit = -1
        this.pivotX = this.left.toFloat()
        this.pivotY = (this.height / 2).toFloat()
    }

    fun setBackListener(listener: OnClickListener?, activity: Activity) {
        if (listener == null) {
            leftArea!!.setOnClickListener { activity.finish() }
        } else {
            leftArea!!.setOnClickListener(listener)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable) return super.onInterceptTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN && isTouchWithinHotspot(event)) {
            isAnimating = true
        }
        return if (isTouchWithinHotspot(event) && isAnimating) true else super.onInterceptTouchEvent(
            event
        )
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isTouchWithinHotspot(event)) {
            isAnimating = true
        }
        val shouldDispatch = isAnimating
        return if (!shouldDispatch) {
            true
        } else {
            if (shouldDispatch && isTouchWithinHotspot(event)) super.dispatchTouchEvent(event) else super.dispatchTouchEvent(
                event
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable) return super.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (isAnimating) {
                animateDown()
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            if (!isTouchWithinHotspot(event)) {
                isAnimating = false
                animateUp()
                return true
            }
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            isAnimating = false
            if (isTouchWithinHotspot(event)) {
                leftArea!!.callOnClick()
            }
            animateUp()
        }
        return super.onTouchEvent(event)
    }

    fun setTitle(title: String?) {
        titleTextView!!.text = title
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateDown() {
        if (this.isClickable) {
            val animatorSet = AnimatorSet()
            animatorSet.duration = 66L
            val scaleXAnimator = ObjectAnimator.ofFloat(leftArea, "scaleX", 1.0f, 0.8f)
            val scaleYAnimator = ObjectAnimator.ofFloat(leftArea, "scaleY", 1.0f, 0.8f)
            val alphaAnimator = ObjectAnimator.ofInt(leftArea, "Alpha", 50, 250)
            animatorSet.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator)
            animatorSet.start()
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateUp() {
        if (this.isClickable) {
            val animatorSet = AnimatorSet()
            animatorSet.duration = 333L
            val scaleXAnimator = ObjectAnimator.ofFloat(leftArea, "scaleX", 0.8f, 1.0f)
            val scaleYAnimator = ObjectAnimator.ofFloat(leftArea, "scaleY", 0.8f, 1.0f)
            val alphaAnimator = ObjectAnimator.ofInt(leftArea, "Alpha", 250, 50)
            animatorSet.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator)
            animatorSet.start()
        }
    }

    private fun isTouchWithinHotspot(event: MotionEvent): Boolean {
        return event.rawX <= this.context.resources.getDimensionPixelSize(R.dimen.title_bar_right_hotspot)
            .toFloat()
    }
}