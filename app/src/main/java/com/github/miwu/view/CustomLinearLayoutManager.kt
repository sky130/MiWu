package com.github.miwu.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun canScrollVertically() = false
}

class SmoothLinearLayoutManager @JvmOverloads constructor(
    context: Context, orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context) {

    override fun canScrollVertically() = false

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
//        scroll
        return 0
    }

}
