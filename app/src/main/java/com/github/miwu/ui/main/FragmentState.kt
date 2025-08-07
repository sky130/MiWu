package com.github.miwu.ui.main

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion

sealed class FragmentState {
    data object Empty : FragmentState()
    data object Error : FragmentState()
    data object Normal : FragmentState()

    data object Loading : FragmentState()

    fun toVisibility(reverse: Boolean = false) = if (!reverse) when (this) {
        Empty -> View.VISIBLE
        Error -> View.VISIBLE
        Normal -> View.GONE
        Loading -> View.GONE
    } else when (this) {
        Empty -> View.GONE
        Error -> View.GONE
        Normal -> View.VISIBLE
        Loading -> View.GONE
    }
}

@BindingAdapter(value = ["state", "reverse"])
fun LinearLayout.convertFragmentStateToVisibility(state: FragmentState,reverse: Boolean){
    this.visibility = state.toVisibility(reverse)
}
