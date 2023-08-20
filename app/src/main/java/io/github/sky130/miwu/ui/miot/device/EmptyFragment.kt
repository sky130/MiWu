package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.DeviceEmptyBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils

class EmptyFragment : BaseFragment() {
    private lateinit var binding: DeviceEmptyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceEmptyBinding.inflate(inflater)
        var url = ""
        var isOnline = false
        HomeDAO.getHome(HomeDAO.getHomeIndex())?.deviceList?.forEach { device ->
            if (device.model == getModel()) {
                url = device.iconUrl
                isOnline = device.isOnline
            }
        }
        if (url.isNotEmpty()) GlideUtils.loadImg(url, binding.deviceImage)
        if (isOnline) binding.deviceStatus.text =
            getString(R.string.device_online) else binding.deviceStatus.text =
            getString(R.string.device_offline)

        return binding.root
    }
}