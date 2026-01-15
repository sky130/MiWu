package com.github.miwu.view.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.isVisible(isVisible: Boolean?) {
    this.isVisible = isVisible ?: true
}