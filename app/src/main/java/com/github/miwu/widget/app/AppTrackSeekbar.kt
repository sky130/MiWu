package com.github.miwu.widget.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.miwu.R
import kndroidx.extension.dp

@SuppressLint("UseCompatLoadingForDrawables")
class AppTrackSeekbar(
    context: Context,
    attr: AttributeSet,
) : AppCompatSeekBar(context, attr) {
    private var minProgress: Int = 0
    private var maxProgress: Int = 100

//    init {
//        splitTrack = false
//        isDuplicateParentStateEnabled = true
//    }

    fun getCurrentProgress(): Int {
        return getCurrentProgress(progress)
    }

    fun setCurrentProgress(progress: Int) {
        this.progress = getRelativeProgress(progress)
    }

    private fun getCurrentProgress(progress: Int): Int {
        val currentProgress =
            (progress.toFloat() / 100f) * (maxProgress - minProgress) + minProgress
        return currentProgress.toInt()
    }

    private fun getRelativeProgress(progress: Int): Int {
        val currentProgress =
            (progress.toFloat() - minProgress) / (maxProgress - minProgress) * 100f
        return currentProgress.toInt()
    }

    fun setMaxProgress(max: Int) {
        this.maxProgress = max
    }

    fun setMinProgress(min: Int) {
        this.minProgress = min
    }

}