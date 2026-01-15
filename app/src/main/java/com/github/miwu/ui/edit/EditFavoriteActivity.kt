package com.github.miwu.ui.edit

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.databinding.ActivityEditFavoriteBinding as Binding
import kndroidx.activity.ViewActivityX
import kndroidx.databinding.recycler.BaseBindingAdapter
import kotlinx.coroutines.launch
import miwu.support.base.MiwuDevice
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class EditFavoriteActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: EditFavoriteViewModel by viewModel()
    val itemTouchHelper = ItemTouchHelper(TouchCallback())

    @get:Suppress("UNCHECKED_CAST")
    val adapter get() = binding.recycler.adapter as? BaseBindingAdapter<FavoriteDevice, ViewBinding>
    val list get() = adapter?.item

    override fun onDestroy() {
        list?.let(viewModel::updateSortIndices)
        super.onDestroy()
    }

    private inner class TouchCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.absoluteAdapterPosition
            val to = target.absoluteAdapterPosition
            list?.let { Collections.swap(it, from, to) }
            recyclerView.adapter?.notifyItemMoved(from, to)
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            val view = viewHolder.itemView
            view.animateScale(1f, 1f, 200L)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            val view = viewHolder?.itemView ?: return
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                view.animateScale(1.03f, 0.7f, 200L)
        }

        private fun View.animateScale(scale: Float, alpha: Float, duration: Long) =
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(this@animateScale, "scaleX", scale),
                    ObjectAnimator.ofFloat(this@animateScale, "scaleY", scale),
                    ObjectAnimator.ofFloat(this@animateScale, "alpha", alpha)
                )
                this.duration = duration
                start()
            }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
    }
}