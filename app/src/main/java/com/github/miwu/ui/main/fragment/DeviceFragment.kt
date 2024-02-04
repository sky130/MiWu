package com.github.miwu.ui.main.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices

class DeviceFragment : ViewFragmentX<FragmentMainDeviceBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {
    val isEmpty = MutableLiveData(false)

    override fun init() {
        binding.swipe.setOnRefreshListener(this)
        AppRepository.deviceFlow.onEach {
            isEmpty.value = it.isEmpty()
        }.launchIn(lifecycleScope)
        AppRepository.deviceRefreshFlow.onEach {
            binding.swipe.isRefreshing = false
        }.launchIn(lifecycleScope)
    }

    fun getRoomName(item: Any?) = AppRepository.getRoomName(item as MiotDevices.Result.Device)

    fun onItemClick(item: Any?) {
        item as MiotDevices.Result.Device
        if (item.isOnline) requireContext().startDeviceActivity(item)
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotDevices.Result.Device
        return true
    }

    override fun onRefresh() {
        AppRepository.updateDevice()
    }
}