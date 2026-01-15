package com.github.miwu.ui.main.fragment

import com.github.miwu.utils.Logger
import com.github.miwu.databinding.FragmentMainDeviceBinding as Binding
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.ui.main.MainViewModel
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDevices
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()
    val user get() = appRepository.miotUser.also(::checkMiotUser)
    val appRepository get() = viewModel.appRepository
    val logger = Logger()

    fun onItemClick(item: Any?) {
        if (item !is MiotDevice || !item.isOnline || user == null) return
        val user = user ?: return
        requireContext().startDeviceActivity(item, user)
    }

    private fun checkMiotUser(miotUser: MiotUser?) {
        if (miotUser != null) return
        logger.error("MiotUser is null")
        "MiotUser is null".toast()
    }
}