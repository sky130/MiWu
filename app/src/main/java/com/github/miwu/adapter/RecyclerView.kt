package com.github.miwu.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("adapter", "itemTouchHelper", requireAll = false)
fun RecyclerView.adapter(adapter: RecyclerView.Adapter<*>?, itemTouchHelper: ItemTouchHelper?) {
    this.adapter = adapter
    itemTouchHelper?.attachToRecyclerView(this)
}