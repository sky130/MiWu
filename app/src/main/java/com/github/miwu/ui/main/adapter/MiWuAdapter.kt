package com.github.miwu.ui.main.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.miwu.databinding.ItemMiMiwuDeviceBinding
import com.github.miwu.logic.database.model.MiwuDatabaseDevice
import com.github.miwu.logic.database.model.MiwuDatabaseDevice.Companion.toMiot
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.adapter.loadMiotIcon
import com.github.miwu.ui.main.MainViewModel
import java.util.*

class MiWuAdapter(val viewModel: ViewModel,val appRepository: AppRepository) : RecyclerView.Adapter<MiWuAdapter.ViewHolder>() {
    var onLongClickBlock: ((Int) -> Unit)? = null
    var onClickBlock: ((Int) -> Unit)? = null
    var list = arrayListOf<MiwuDatabaseDevice>()

    class ViewHolder(val binding: ItemMiMiwuDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: ArrayList<MiwuDatabaseDevice>) {
        DiffUtil.calculateDiff(MiWuDiffCallback(list, newList)).dispatchUpdatesTo(this)
        list = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemMiMiwuDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.binding.bind(position, list[position])

    private fun ItemMiMiwuDeviceBinding.bind(position: Int, device: MiwuDatabaseDevice) {
        itemIcon.loadMiotIcon(device, viewModel, appRepository.miotClient)
        itemName.text = device.name
        itemRoom.text = appRepository.getRoomName(device.toMiot())
        root.setOnClickListener { onClickBlock?.invoke(position) }
        root.setOnLongClickListener { onLongClickBlock?.invoke(position); false }
    }

    inner class MiWuDiffCallback(
        private val oldList: List<MiwuDatabaseDevice>,
        private val newList: List<MiwuDatabaseDevice>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].did == newList[newItemPosition].did

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    class TouchCallback(private val adapter: MiWuAdapter) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val isGrid = recyclerView.layoutManager is GridLayoutManager
            return makeMovementFlags(
                if (isGrid) ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                else ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                if (isGrid) 0 else ItemTouchHelper.START
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.absoluteAdapterPosition
            val to = target.absoluteAdapterPosition
            Collections.swap(adapter.list, from, to)
            recyclerView.adapter?.notifyItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.START) {
                adapter.list.removeAt(viewHolder.absoluteAdapterPosition)
                adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) viewHolder?.itemView?.animateScale(
                1.03f,
                0.7f,
                200L
            )
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.animateScale(1f, 1f, 250L)
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
    }
}
