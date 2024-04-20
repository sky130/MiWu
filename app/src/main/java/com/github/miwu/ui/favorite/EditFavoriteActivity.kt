package com.github.miwu.ui.favorite

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback
import androidx.recyclerview.widget.ItemTouchHelper.Callback.*
import com.github.miwu.MainApplication.Companion.appScope
import com.github.miwu.databinding.ActivityEditFavoriteBinding
import com.github.miwu.databinding.ItemMiMiwuDeviceBinding
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.main.adapter.MiWuAdapter
import com.github.miwu.viewmodel.EditFavoriteViewModel
import com.github.miwu.widget.adapter.loadMiotIcon
import kndroidx.activity.ViewActivityX
import kndroidx.extension.compareTo
import kndroidx.extension.copy
import kndroidx.extension.log
import kndroidx.extension.toast
import kndroidx.recycler.live.liveAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class EditFavoriteActivity : ViewActivityX<ActivityEditFavoriteBinding, EditFavoriteViewModel>() {

    private val adapter by lazy { MiWuAdapter(viewModel) }
    private val itemTouchHelper by lazy { ItemTouchHelper(MiWuAdapter.TouchCallback(adapter)) }


    override fun init() {
        "左滑可以删除设备".toast()
        binding.recycler.liveAdapter = liveAdapter
//        binding.recycler.adapter = adapter
//        itemTouchHelper.attachToRecyclerView(binding.recycler)
//        viewModel.viewModelScope.launch {
//            viewModel.deviceFlow.collectLatest {
//                val list = ArrayList(it)
//                list.sortBy { it.index }
//                adapter.updateList(list)
//            }
//        }
    }


    val liveAdapter by liveAdapter<MiwuDevice>(scope = lifecycleScope) {
        items(viewModel.deviceFlow) {
            bind<MiwuDevice, ItemMiMiwuDeviceBinding> {
                itemIcon.loadMiotIcon(it, viewModel)
                itemName <= it.name
                itemRoom <= AppRepository.getRoomName(it)
            }
        }
        diff {
            areContentsTheSame { old, new ->
                old.did == new.did
            }
        }
        touch {
            movementFlags { view, holder ->
                makeMovementFlags(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.START)
            }

            onMove { view, holder, target ->
                val cacheList = list.toMutableList()
                val fromPosition = holder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(cacheList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(cacheList, i, i - 1)
                    }
                }
                list = cacheList
                true
            }

            onSwiped { holder, direction ->
                val position = holder.absoluteAdapterPosition
                if (direction == ItemTouchHelper.START) {
                    adapter.list.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }

            onSelectedChanged { viewHolder, actionState ->
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.animateStart()
                }
            }

            clearView { view, holder ->
                holder.itemView.animateEnd()
            }
        }
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

    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        appScope.launch {
            viewModel.saveList(ArrayList(liveAdapter.list))
        }
    }
}