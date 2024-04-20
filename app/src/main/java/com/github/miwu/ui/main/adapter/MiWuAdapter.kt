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
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.widget.adapter.loadMiotIcon
import kndroidx.extension.compareTo
import java.util.Collections


class MiWuAdapter(private val viewModel: ViewModel) :
    RecyclerView.Adapter<MiWuAdapter.ViewHolder>() {
    var onLongClickBlock: ((Int) -> Unit)? = null
    var onClickBlock: ((Int) -> Unit)? = null

    class ViewHolder(val binding: ItemMiMiwuDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    var list: ArrayList<MiwuDevice> = arrayListOf()

    fun updateList(newList: ArrayList<MiwuDevice>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(list, newList))
        diffResult.dispatchUpdatesTo(this)
        this.list = newList
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.onBind(position, list[position])
    }

    private fun ItemMiMiwuDeviceBinding.onBind(position: Int, device: MiwuDevice) {
        itemIcon.loadMiotIcon(device, viewModel)
        itemName <= device.name
        itemRoom <= AppRepository.getRoomName(device)
        root.setOnClickListener { onClickBlock?.let { it1 -> it1(position) } }
        root.setOnLongClickListener { onLongClickBlock?.let { it1 -> it1(position) };false }
    }

    inner class MyDiffCallback(
        private val oldList: List<MiwuDevice>,
        private val newList: List<MiwuDevice>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].did == newList[newItemPosition].did
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


    class TouchCallback(private val adapter: MiWuAdapter) : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlag: Int
            val swipeFlag: Int
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is GridLayoutManager) {
                dragFlag = (ItemTouchHelper.DOWN or ItemTouchHelper.UP
                        or ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)
                swipeFlag = 0
            } else {
                dragFlag = ItemTouchHelper.DOWN or ItemTouchHelper.UP
                swipeFlag = ItemTouchHelper.START
            }
            return makeMovementFlags(dragFlag, swipeFlag)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(adapter.list, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(adapter.list, i, i - 1)
                }
            }
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.START) {
                adapter.list.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }


        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.animateStart()
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.animateEnd()
        }

        private fun View.animateEnd() {
            val animatorSet = AnimatorSet()
            animatorSet.duration = 250L
            val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.03f, 1.0f)
            val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.03f, 1.0f)
            val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 0.7f, 1.0f)
            animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
            animatorSet.start()
        }

        private fun View.animateStart() {
            val animatorSet = AnimatorSet()
            animatorSet.duration = 200L
            val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.03f)
            val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.03f)
            val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.7f)
            animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
            animatorSet.start()
        }
    }


}