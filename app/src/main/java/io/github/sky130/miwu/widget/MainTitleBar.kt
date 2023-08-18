package io.github.sky130.miwu.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import io.github.sky130.miwu.R

class MainTitleBar(context: Context, attributeSet: AttributeSet?) : LinearLayout(context, attributeSet) {
    var titleTextView: TextView
    var title: String?

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MiTitleBar, 0, 0)
        title = typedArray.getString(R.styleable.MiTitleBar_title)
        typedArray.recycle()
        this.gravity = 16
        inflate(context, R.layout.main_title_bar, this)
        setPadding(
            context.resources.getDimensionPixelSize(R.dimen.content_horizontal_distance),
            0,
            0,
            0
        )
        titleTextView = findViewById(R.id.textView)
        titleTextView.text = title
        titleTextView.isSelected = true
        titleTextView.marqueeRepeatLimit = -1
    }

    fun setTitle(var1: String?) {
        titleTextView.text = var1
    }
}