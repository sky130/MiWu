package com.github.miwu.ui.main.fragment

import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.ui.main.MainViewModel
import com.github.miwu.utils.Logger
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.miot.MiotDevice
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.FragmentMainDeviceBinding as Binding

class DeviceFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()
    val logger = Logger()

    fun onItemClick(item: Any?) {
        if (item !is MiotDevice || !item.isOnline) return
        requireContext().startDeviceActivity(item)
    }
}
