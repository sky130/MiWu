package com.github.miwu.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import com.github.miwu.R
import androidx.appcompat.widget.AppCompatImageView

open class MiSwitchButton(context: Context, attr: AttributeSet) :
    AppCompatImageView(context, attr), OnClickListener {
    private var isChecked = false
    private var listener: ((Boolean) -> Unit)? = null

    init {
        reset()
    }

    override fun onClick(view: View) {
        isChecked = !isChecked
        listener?.invoke(isChecked)
        reset()
    }

    fun setChecked(boolean: Boolean) {
        isChecked = boolean
        reset()
    }

    private fun reset() {
        if (isChecked) {
            setBackgroundResource(R.drawable.bg_swicth_button_on)
        } else {
            setBackgroundResource(R.drawable.bg_swicth_button_off)
        }
    }

    fun setOnCheckedListener(listener: ((Boolean) -> Unit)?) {
        this.listener = listener
    }

}