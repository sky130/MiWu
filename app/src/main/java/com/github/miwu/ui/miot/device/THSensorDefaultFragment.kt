package com.github.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.databinding.DeviceThSensorDefaultBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.logic.model.miot.MiotService
import com.github.miwu.logic.network.MiotSpecService
import com.github.miwu.ui.manager.MiWidgetManager
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.util.GlideUtils

// 全程Temperature Humidity Sensor
class THSensorDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceThSensorDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceThSensorDefaultBinding.inflate(inflater)
        manager.setDid(getDid())
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
        for (i in miotServices) {
            val type = MiotSpecService.parseUrn(i.type)?.value ?: continue
            if (type != "temperature-humidity-sensor") continue
            val siid = i.iid
            for (x in i.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when (type2) {
                    "temperature" -> {
                        manager.addView(binding.temperature, type2, siid, piid, 0)
                    }

                    "relative-humidity" -> {
                        manager.addView(binding.relativeHumidity, type2, siid, piid, 0)
                    }
                }
            }
        }
        for (i in miotServices) {
            val type = MiotSpecService.parseUrn(i.type)?.value ?: continue
            if (type != "battery") continue
            val siid = i.iid
            for (x in i.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when (type2) {
                    "battery-level" -> {
                        manager.addView(binding.battery, type2, siid, piid, 0)
                    }
                }
            }
        }
        manager.init()
        manager.update()
        binding.root.requestFocus()
        return binding.root
    }

}