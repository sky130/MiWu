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


class MiIndicators(
    context: Context,
    attr: AttributeSet,
) : View(context, attr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotWidth = 3f.toPx() //圆的宽度
    private val dotMargin = 8f.toPx()

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
    }

    private val dotSize: Int // 点数量
    private var dotIndex = 0 // 点索引

    init {
        context.obtainStyledAttributes(attr, R.styleable.MiIndicators).apply {
            dotSize = getInt(R.styleable.MiIndicators_dotSize, 0)
            recycle()
        }
    }

    fun setIndex(index: Int) {
        dotIndex = index
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec) // 设置为屏幕宽度
        setMeasuredDimension(widthMeasureSpec, dotWidth + 4f.toPx())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 先计算屏幕正中心位置
        val centerHeight = (height / 2).toFloat()
        val centerWidth = (width / 2).toFloat()
        paint.strokeWidth = dotWidth.toFloat()
        val drawWidth = dotWidth + dotMargin
        val startX =
            centerWidth - ((dotSize - 1) / 2 * dotWidth + (dotSize - 1) / 2 * dotMargin)// 开始的坐标位
        var drawX = startX
        for (i in 0 until dotSize) {
            if (i == dotIndex) {
                paint.color = ContextCompat.getColor(context, R.color.white)
            } else {
                paint.color = ContextCompat.getColor(context, R.color.lrc_disable)
            }
            canvas.drawCircle(drawX, centerHeight, centerHeight, paint)
            drawX += drawWidth
        }
    }

    private fun Float.toPx(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

}