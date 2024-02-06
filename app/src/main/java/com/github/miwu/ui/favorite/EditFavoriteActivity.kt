package com.github.miwu.ui.favorite

import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.github.miwu.MainApplication.Companion.appScope
import com.github.miwu.databinding.ActivityEditFavoriteBinding
import com.github.miwu.ui.main.adapter.MiWuAdapter
import com.github.miwu.viewmodel.EditFavoriteViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.toast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditFavoriteActivity : ViewActivityX<ActivityEditFavoriteBinding, EditFavoriteViewModel>() {

    private val adapter by lazy { MiWuAdapter(viewModel) }
    private val itemTouchHelper by lazy { ItemTouchHelper(MiWuAdapter.TouchCallback(adapter)) }

    override fun init() {
        "左滑可以删除设备".toast()
        binding.recycler.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.recycler)
        viewModel.viewModelScope.launch {
            viewModel.deviceFlow.collectLatest {
                val list = ArrayList(it)
                list.sortBy { it.index }
                adapter.updateList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        appScope.launch {
            viewModel.saveList(adapter.list)
        }
    }
}