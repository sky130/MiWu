package miwu.android.view.app

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class NoPaddingTextView : AppCompatTextView {
    private var mAdditionalPadding = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    private fun init() {
        includeFontPadding = false
    }

    override fun onDraw(canvas: Canvas) {
        val yOff = -mAdditionalPadding / 6
        canvas.translate(0f, yOff.toFloat())
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, mHeightMeasureSpec: Int) {
        var heightMeasureSpec = mHeightMeasureSpec
        additionalPadding
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode != MeasureSpec.EXACTLY) {
            val measureHeight = measureHeight(getText().toString(), widthMeasureSpec)
            var height = measureHeight - mAdditionalPadding
            height += paddingTop + paddingBottom
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun measureHeight(text: String, widthMeasureSpec: Int): Int {
        val textSize = textSize
        val textView = TextView(context)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView.text = text
        textView.measure(widthMeasureSpec, 0)
        return textView.measuredHeight
    }

    private val additionalPadding: Int
        get() {
            val textSize = textSize
            val textView = TextView(context)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            textView.setLines(1)
            textView.measure(0, 0)
            val measuredHeight = textView.measuredHeight
            if (measuredHeight - textSize > 0) {
                mAdditionalPadding = (measuredHeight - textSize - paddingBottom).toInt()
            }
            return mAdditionalPadding
        }
}