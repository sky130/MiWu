package com.github.miwu.ui.main.fragment

import com.github.miwu.databinding.FragmentMainDeviceBinding as Binding
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.ui.main.MainViewModel
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.miot.MiotDevices
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceFragment : ViewFragmentX<Binding>(Binding::inflate){
    override val viewModel: MainViewModel by viewModel()

    fun onItemClick(item: Any?) {
        when (item) {
            is MiotDevices.Result.Device -> {
                if (item.isOnline) requireContext().startDeviceActivity(item)
            }
        }
    }

    fun onItemLongClick(item: Any?): Boolean {
        when (item) {
            is MiotDevices.Result.Device -> {

            }
        }
        return true
    }
}