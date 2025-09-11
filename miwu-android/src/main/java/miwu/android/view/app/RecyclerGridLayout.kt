package miwu.android.view.app

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerGridLayout(context: Context, attr: AttributeSet) : RecyclerView(context, attr) {
    private val layoutManager = GridLayoutManager(context, 2)
    private val adapter = GridAdapter()
    private val viewList = mutableListOf<View>()

    fun add(view: View){
        viewList.add(view)
        adapter.notifyItemInserted(viewList.size - 1)
    }

    init {
        setAdapter(adapter)
        setLayoutManager(layoutManager)
        setItemViewCacheSize(100)
        addItemDecoration(SpacingItemDecoration(5))
    }

    inner class GridViewHolder(val container: FrameLayout) : ViewHolder(container)

    inner class GridAdapter : Adapter<GridViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GridViewHolder {
            val frameLayout = FrameLayout(parent.context)
            frameLayout.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            return GridViewHolder(frameLayout)
        }

        override fun onBindViewHolder(
            holder: GridViewHolder,
            position: Int
        ) {
            val view = viewList[position]
            holder.container.removeAllViews()
            holder.container.addView(view)
        }

        override fun getItemCount(): Int = viewList.size
    }

    class SpacingItemDecoration(private val spacing: Int) : ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val spacingPx = dpToPx(spacing, parent.context)
            outRect.left = spacingPx
            outRect.right = spacingPx
            outRect.bottom = spacingPx
        }

        private fun dpToPx(dp: Int, context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }

}