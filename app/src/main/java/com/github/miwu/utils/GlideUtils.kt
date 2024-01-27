package com.github.miwu.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.miwu.R

fun ImageView.setImageUrl(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miwu_placeholder)
        .into(this)
}