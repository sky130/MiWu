package com.github.miwu.ui.favorite

import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.github.miwu.MainApplication.Companion.appScope
import com.github.miwu.ui.main.adapter.MiWuAdapter
import kndroidx.activity.ViewActivityX
import kndroidx.extension.toast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityEditFavoriteBinding as Binding

class EditFavoriteActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: EditFavoriteViewModel by viewModel()
    val adapter by lazy { MiWuAdapter(viewModel,viewModel.appRepository) }
    val itemTouchHelper by lazy { ItemTouchHelper(MiWuAdapter.TouchCallback(adapter)) }


    override fun init() {
        "左滑删除 长按排序".toast()
        viewModel.viewModelScope.launch {
            viewModel.deviceFlow.collectLatest {
                val list = ArrayList(it)
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
            viewModel.saveList(ArrayList(adapter.list))
        }
    }
}
