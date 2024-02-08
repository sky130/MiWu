package com.github.miwu.widget.app

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat
import kotlin.math.roundToInt
import androidx.wear.widget.WearableRecyclerView as RV

class SmoothRecyclerView(context: Context, attr: AttributeSet) : RV(context, attr) {

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return if (event.action == MotionEvent.ACTION_SCROLL && event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)) {
            val delta =
                -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) * ViewConfigurationCompat.getScaledVerticalScrollFactor(
                    ViewConfiguration.get(context), context
                )
            smoothScrollBy(0, delta.roundToInt())
            true
        } else {
            false
        }
    }

}