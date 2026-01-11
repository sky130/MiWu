package miwu.android.view.app

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar

/**
 * 兼容层, 在较旧的 Android 版本上不支持设置 min
 */
open class BasicSeekbar(
    context: Context,
    attr: AttributeSet,
) : AppCompatSeekBar(context, attr) {
    private var minProgress: Int = 0
    private var maxProgress: Int = 100

    override fun setMin(min: Int) {
        minProgress = min
    }

    override fun setMax(max: Int) {
        maxProgress = max
    }

    override fun getProgress(): Int =
        ((super.getProgress().toFloat() / 100f) * (maxProgress - minProgress) + minProgress).toInt()

    override fun setProgress(progress: Int) {
        super.setProgress(((progress.toFloat() - minProgress) / (maxProgress - minProgress) * 100f).toInt())
    }
}