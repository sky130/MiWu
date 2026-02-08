package com.github.miwu.ui.main.state

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter

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

@BindingAdapter(value = ["state", "reverse"], requireAll = false)
fun View.convertFragmentStateToVisibility(state: FragmentState?, reverse: Boolean? = false) {
    state?.toVisibility(reverse ?: false)?.let {
        this.visibility = it
    }
}
