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
        isNestedScrollingEnabled = false
        addItemDecoration(GridSpacingItemDecoration(spacing = 5, spanCount = 2))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (viewList.isEmpty()) {
            return
        }

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val availableWidth = width - paddingLeft - paddingRight

        var totalHeight = 0

        for (i in viewList.indices) {
            val view = viewList[i]
            val column = i % 2

            val childWidthSpec = MeasureSpec.makeMeasureSpec(
                availableWidth / 2,
                MeasureSpec.EXACTLY
            )
            val childHeightSpec = MeasureSpec.makeMeasureSpec(
                0,
                MeasureSpec.UNSPECIFIED
            )

            view.measure(childWidthSpec, childHeightSpec)

            if (column == 0) {
                totalHeight += view.measuredHeight + 5
            }
        }

        setMeasuredDimension(
            width,
            totalHeight + paddingTop + paddingBottom
        )
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
            outRect.bottom = spacingPx
        }

        private fun dpToPx(dp: Int, context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }

    class GridSpacingItemDecoration(
        private val spacing: Int,
        private val spanCount: Int = 2,
        private val includeEdge: Boolean = false,
        private val orientation: Int = GridLayoutManager.VERTICAL
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val spacingPx = dpToPx(spacing, parent.context)
            val position = parent.getChildAdapterPosition(view)

            if (position == RecyclerView.NO_POSITION) return

            val column = position % spanCount
            val row = position / spanCount

            if (orientation == GridLayoutManager.VERTICAL) {
                if (includeEdge) {
                    outRect.left = spacingPx - column * spacingPx / spanCount
                    outRect.right = (column + 1) * spacingPx / spanCount

//                    if (row == 0) {
//                        outRect.top = spacingPx
//                    }
                    outRect.bottom = spacingPx
                } else {
                    outRect.left = column * spacingPx / spanCount
                    outRect.right = spacingPx - (column + 1) * spacingPx / spanCount

                    if (row != 0) {
                        outRect.top = spacingPx
                    }
                }
            } else {
                if (includeEdge) {
                    outRect.top = spacingPx - row * spacingPx / spanCount
                    outRect.bottom = (row + 1) * spacingPx / spanCount

                    if (column == 0) {
                        outRect.left = spacingPx
                    }
                    outRect.right = spacingPx
                } else {
                    outRect.top = row * spacingPx / spanCount
                    outRect.bottom = spacingPx - (row + 1) * spacingPx / spanCount

                    if (column != 0) {
                        outRect.left = spacingPx
                    }
                }
            }
        }

        private fun dpToPx(dp: Int, context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }

}