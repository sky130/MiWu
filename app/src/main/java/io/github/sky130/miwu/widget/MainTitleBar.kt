package io.github.sky130.miwu.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import io.github.sky130.miwu.R

class MainTitleBar(var1: Context, var2: AttributeSet?) : LinearLayout(var1, var2) {
    var titleTextView: TextView
    var b: String?

    init {
        val var10009 = var1.obtainStyledAttributes(var2, R.styleable.MiTitleBar, 0, 0)
        b = var10009.getString(R.styleable.MiTitleBar_title)
        var10009.recycle()
        this.gravity = 16
        inflate(var1, R.layout.main_title_bar, this)
        setPadding(
            var1.resources.getDimensionPixelSize(R.dimen.content_horizontal_distance),
            0,
            0,
            0
        )
        titleTextView = findViewById(R.id.textView)
        titleTextView.text = b
        titleTextView.isSelected = true
        titleTextView.marqueeRepeatLimit = -1
    }

    fun setTitle(var1: String?) {
        titleTextView.text = var1
    }
}