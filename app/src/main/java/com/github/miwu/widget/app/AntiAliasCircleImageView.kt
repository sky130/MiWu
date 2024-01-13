package com.github.miwu.widget.app

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AntiAliasCircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    private val rectF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 强制将宽度和高度设置为相同的值，以保持圆形形状
        val size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        // 获取 ImageView 中的 bitmap
        val bitmap = getBitmapFromDrawable(drawable)

        if (bitmap != null) {
            // 绘制圆形
            path.reset()
            rectF.set(0f, 0f, width.toFloat(), height.toFloat())
            path.addRoundRect(rectF, width / 2f, height / 2f, Path.Direction.CW)
            canvas.clipPath(path)

            // 缩放并绘制图片，保持原始图片的宽高比例
            val scaledBitmap = scaleBitmap(bitmap, width, height)
            canvas.drawBitmap(scaledBitmap, null, rectF, paint)
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val targetRatio = width.toFloat() / height
        val sourceRatio = bitmap.width.toFloat() / bitmap.height

        var scaleX = 1f
        var scaleY = 1f

        if (sourceRatio > targetRatio) {
            scaleX = targetRatio / sourceRatio
        } else {
            scaleY = sourceRatio / targetRatio
        }

        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
