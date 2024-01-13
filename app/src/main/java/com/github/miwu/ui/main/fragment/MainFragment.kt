package com.github.miwu.ui.main.fragment

import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.fragment.ViewFragmentX
import miot.kotlin.model.miot.MiotDevices

class MainFragment : ViewFragmentX<FragmentMainDeviceBinding, MainViewModel>() {

    override fun init() {
        binding.recycler.apply {
            isEdgeItemsCenteringEnabled = true
        }
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
}