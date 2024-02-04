package com.github.miwu.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun canScrollVertically() = false
}