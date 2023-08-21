package com.github.miwu.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.miwu.R


class MiLineIndicators(
    context: Context,
    attr: AttributeSet,
) : View(context, attr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotWidth: Int
        get() = height
    private val dotMargin: Int
        get() = 8f.toPx() + dotWidth

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.strokeCap = Paint.Cap.ROUND
    }

    private val dotSize: Int // 点数量
    private var dotIndex = 0 // 点索引

    init {
        context.obtainStyledAttributes(attr, R.styleable.MiLineIndicators).apply {
            dotSize = getInt(R.styleable.MiLineIndicators_dotSize, 0)
            recycle()
        }
    }

    fun setIndex(index: Int) {
        dotIndex = index
        requestLayout()
        invalidate()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec) // 设置为屏幕宽度
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 先计算屏幕正中心位置
        val centerHeight = height / 2f
        paint.strokeWidth = dotWidth.toFloat()
        val startX = dotMargin / 2f
        val lineSize = (((width).toFloat() - dotMargin * (dotSize)) / dotSize)
        var drawX = startX
        for (i in 0 until dotSize) {
            if (i == dotIndex) {
                paint.color = ContextCompat.getColor(context, R.color.white)
            } else {
                paint.color = ContextCompat.getColor(context, R.color.lrc_disable)
            }
            canvas.drawLine(drawX, centerHeight, drawX + lineSize, centerHeight, paint)
            drawX += lineSize + dotMargin
        }
    }

    private fun Float.toPx(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

}