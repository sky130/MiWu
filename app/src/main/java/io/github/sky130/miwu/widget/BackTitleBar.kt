package io.github.sky130.miwu.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import io.github.sky130.miwu.R

@SuppressLint("CustomViewStyleable")
class BackTitleBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int = 0) :
    LinearLayout(context, attributeSet, defStyleAttr) {
    var backIcon: ImageView? = null
    var leftArea: LinearLayout? = null
    var textClock: TextClock? = null
    var titleTextView: TextView? = null
    var title: String?
    var f = false

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MiTitleBar, 0, 0)
        title = typedArray.getString(R.styleable.MiTitleBar_title)
        typedArray.recycle()
        this.init(context)
    }

    fun init(context: Context) {
        inflate(context, R.layout.back_title_bar, this)
        this.isClickable = true
        setPadding(
            context.resources.getDimensionPixelSize(R.dimen.content_horizontal_distance),
            0,
            0,
            0
        )
        leftArea = findViewById(R.id.left_area)
        backIcon = findViewById(R.id.left_icon)
        textClock = findViewById(R.id.clock_stub)
        titleTextView = findViewById(R.id.title_textview)
        titleTextView?.text = title
        titleTextView?.isSelected = true
        titleTextView?.marqueeRepeatLimit = -1
        this.pivotX = this.left.toFloat()
        this.pivotY = (this.height / 2).toFloat()
    }

    fun setBackListener(var1: OnClickListener?, var2: Activity) {
        if (var1 == null) {
            leftArea!!.setOnClickListener { var2.finish() }
        } else {
            leftArea!!.setOnClickListener(var1)
        }
    }

    override fun onInterceptTouchEvent(var1: MotionEvent): Boolean {
        if (var1.action == 0 && this.a(var1)) {
            f = true
        }
        return if (this.a(var1) && f) true else super.onInterceptTouchEvent(var1)
    }

    override fun dispatchTouchEvent(var1: MotionEvent): Boolean {
        if (var1.action == 0 && this.a(var1)) {
            f = true
        }
        var var2: Boolean
        return if (!f.also { var2 = it }) {
            true
        } else {
            if (var2 && this.a(var1)) super.dispatchTouchEvent(var1) else super.dispatchTouchEvent(
                var1
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(var1: MotionEvent): Boolean {
        if (var1.action == 0) {
            if (f) {
                b()
            }
        } else if (2 == var1.action) {
            if (!this.a(var1)) {
                f = false
                this.a()
                return true
            }
        } else if (1 == var1.action || 3 == var1.action) {
            f = false
            if (this.a(var1)) {
                leftArea!!.callOnClick()
            }
            this.a()
        }
        return super.onTouchEvent(var1)
    }

    fun setTitle(var1: String?) {
        titleTextView!!.text = var1
    }

    fun b() {
        if (this.isClickable) {
            val var10000 = AnimatorSet()
            var10000.duration = 66L
            val var1 = FloatArray(2)
            var1[0] = 1.0f
            var1[1] = 0.8f
            val var10002 = ObjectAnimator.ofFloat(leftArea, "scaleX", *var1)
            val var6 = this
            val var10004 = leftArea
            val var2 = FloatArray(2)
            var2[0] = 1.0f
            var2[1] = 0.8f
            val var3 = ObjectAnimator.ofFloat(var10004, "scaleY", *var2)
            val var7 = var6.leftArea
            val var4 = IntArray(2)
            var4[0] = 50
            var4[1] = 250
            @SuppressLint("ObjectAnimatorBinding") val var5 =
                ObjectAnimator.ofInt(var7, "Alpha", *var4)
            var10000.play(var10002).with(var3).with(var5)
            var10000.start()
        }
    }

    fun a() {
        if (this.isClickable) {
            val var10000 = AnimatorSet()
            var10000.duration = 333L
            val var1 = FloatArray(2)
            var1[0] = 0.8f
            var1[1] = 1.0f
            val var10002 = ObjectAnimator.ofFloat(leftArea, "scaleX", *var1)
            val var6 = this
            val var10004 = leftArea
            val var2 = FloatArray(2)
            var2[0] = 0.8f
            var2[1] = 1.0f
            val var3 = ObjectAnimator.ofFloat(var10004, "scaleY", *var2)
            val var7 = var6.leftArea
            val var4 = IntArray(2)
            var4[0] = 250
            var4[1] = 50
            @SuppressLint("ObjectAnimatorBinding") val var5 =
                ObjectAnimator.ofInt(var7, "Alpha", *var4)
            var10000.play(var10002).with(var3).with(var5)
            var10000.start()
        }
    }

    fun a(var1: MotionEvent): Boolean {
        return var1.rawX <= this.context.resources.getDimensionPixelSize(R.dimen.title_bar_right_hotspot)
            .toFloat()
    }
}