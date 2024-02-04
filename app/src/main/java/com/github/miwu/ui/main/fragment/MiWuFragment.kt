package com.github.miwu.ui.main.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.github.miwu.databinding.FragmentMainMiwuBinding
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.database.model.MiwuDevice.Companion.toMiot
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.ui.favorite.EditFavoriteActivity
import com.github.miwu.ui.main.adapter.MiWuAdapter
import com.github.miwu.viewmodel.EditFavoriteViewModel
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.extension.log
import kndroidx.extension.start
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import miot.kotlin.model.miot.MiotDevices

class MiWuFragment : ViewFragmentX<FragmentMainMiwuBinding, MainViewModel>() {
    private val adapter by lazy { MiWuAdapter(viewModel) }
    val isEmpty = MutableLiveData(false)

    override fun init() {
        binding.recycler.adapter = adapter
        viewModel.viewModelScope.launch {
            viewModel.deviceFlow.collectLatest {
                val list = ArrayList(it)
                isEmpty.value = list.isEmpty()
                list.sortBy { it.index }
                adapter.updateList(list)
            }
        }
        adapter.onLongClickBlock = {
            requireActivity().start<EditFavoriteActivity>()
        }
        adapter.onClickBlock = {
            requireContext().startDeviceActivity(adapter.list[it].toMiot())
        }
    }

}