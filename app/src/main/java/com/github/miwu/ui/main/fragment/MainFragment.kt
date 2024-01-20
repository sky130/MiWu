package com.github.miwu.ui.main.fragment

import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices

class MainFragment : ViewFragmentX<FragmentMainDeviceBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {

    override fun init() {
        binding.recycler.apply {
            isEdgeItemsCenteringEnabled = true
        }
        binding.swipe.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }

    fun getRoomName(item: Any?) = AppRepository.getRoomName(item as MiotDevices.Result.Device)
    fun onItemClick(item: Any?) {
        item as MiotDevices.Result.Device
        requireContext().startDeviceActivity(item)
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotDevices.Result.Device
        return true
    }

    override fun onRefresh() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            AppRepository.loadDevices()
            withContext(Dispatchers.Main) {
                binding.swipe.isRefreshing = false
                "刷新完成".toast()
            }
        }
    }
}