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
class BackTitleBar @JvmOverloads constructor(var1: Context, var2: AttributeSet?, var3: Int = 0) :
    LinearLayout(var1, var2, var3) {
    var backIcon: ImageView? = null
    var b: LinearLayout? = null
    var textClock: TextClock? = null
    var titleTextView: TextView? = null
    var e: String?
    var f = false

    init {
        val var10002 = var1.obtainStyledAttributes(var2, R.styleable.MiTitleBar, 0, 0)
        e = var10002.getString(R.styleable.MiTitleBar_title)
        var10002.recycle()
        this.a(var1)
    }

    fun a(var1: Context) {
        inflate(var1, R.layout.back_title_bar, this)
        this.isClickable = true
        setPadding(
            var1.resources.getDimensionPixelSize(R.dimen.content_horizontal_distance),
            0,
            0,
            0
        )
        b = findViewById(R.id.left_area)
        backIcon = findViewById(R.id.left_icon)
        textClock = findViewById(R.id.clock_stub)
        titleTextView = findViewById(R.id.title_textview)
        titleTextView?.text = e
        titleTextView?.isSelected = true
        titleTextView?.marqueeRepeatLimit = -1
        this.pivotX = this.left.toFloat()
        this.pivotY = (this.height / 2).toFloat()
    }

    fun setBackListener(var1: OnClickListener?, var2: Activity) {
        if (var1 == null) {
            b!!.setOnClickListener { var2.finish() }
        } else {
            b!!.setOnClickListener(var1)
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
                b!!.callOnClick()
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
            val var10002 = ObjectAnimator.ofFloat(b, "scaleX", *var1)
            val var6 = this
            val var10004 = b
            val var2 = FloatArray(2)
            var2[0] = 1.0f
            var2[1] = 0.8f
            val var3 = ObjectAnimator.ofFloat(var10004, "scaleY", *var2)
            val var7 = var6.b
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
            val var10002 = ObjectAnimator.ofFloat(b, "scaleX", *var1)
            val var6 = this
            val var10004 = b
            val var2 = FloatArray(2)
            var2[0] = 0.8f
            var2[1] = 1.0f
            val var3 = ObjectAnimator.ofFloat(var10004, "scaleY", *var2)
            val var7 = var6.b
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