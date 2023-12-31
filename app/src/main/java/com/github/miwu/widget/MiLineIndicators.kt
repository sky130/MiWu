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
    private var dotMode = 0

    // 0 为点 ,1 为线
    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.strokeCap = Paint.Cap.ROUND
    }

    private var dotSize: Int // 点数量
    private var dotIndex = 2 // 点索引

    fun setDotSize(size: Int) {
        if (size > 7) setDotMode(1)
        dotSize = size
        requestLayout()
        invalidate()
    }

    init {
        context.obtainStyledAttributes(attr, R.styleable.MiLineIndicators).apply {
            dotSize = getInt(R.styleable.MiLineIndicators_dotSize, 0)
            recycle()
        }
    }

    fun setDotMode(mode: Int) {
        dotMode = mode
        requestLayout()
        invalidate()
    }

    fun setIndex(index: Int) {
        if (index > dotSize || index < 0) return
        dotIndex = index
        requestLayout()
        invalidate()
    }

    fun getIndex() = dotIndex

    fun getSize() = dotSize

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 先计算屏幕正中心位置
        val centerHeight = height.toFloat() / 2f
        paint.strokeWidth = dotWidth.toFloat()
        val startX = dotMargin / 2f
        val lineSize = (((width).toFloat() - dotMargin * (dotSize)) / dotSize)
        var drawX = startX
        when (dotMode) {
            0 -> {
                for (i in 1 until dotSize + 1) {
                    if (i <= dotIndex) {
                        paint.color = ContextCompat.getColor(context, R.color.white)
                    } else {
                        paint.color = ContextCompat.getColor(context, R.color.lrc_disable)
                    }
                    canvas.drawLine(drawX, centerHeight, drawX + lineSize, centerHeight, paint)
                    drawX += lineSize + dotMargin
                }
            }

            1 -> {
                paint.color = ContextCompat.getColor(context, R.color.lrc_disable)
                canvas.drawLine(drawX, centerHeight, width.toFloat() - startX, centerHeight, paint)
                if (dotIndex <= 0) return
                paint.color = ContextCompat.getColor(context, R.color.white)
                drawX = dotIndex * (lineSize + dotMargin) - dotMargin
                canvas.drawLine(startX, centerHeight, drawX, centerHeight, paint)
            }
        }
    }

    private fun Float.toPx(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

}