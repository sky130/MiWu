package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.DeviceOutletDefaultBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.miot.MiotService
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.ui.manager.MiWidgetManager
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils

class OutletDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceOutletDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DeviceOutletDefaultBinding.inflate(inflater)
        manager.setDid(getDid())
        var url = ""
        HomeDAO.getHome(HomeDAO.getHomeIndex())?.deviceList?.forEach { device ->
            if (device.model == getModel()) {
                url = device.iconUrl
            }
        }
        if (url.isNotEmpty()) GlideUtils.loadImg(url, binding.deviceImage)
        for (service in miotServices) { // 遍历服务
            val siid = service.iid // 获取当前服务的iid
            val serviceType = MiotSpecService.parseUrn(service.type)?.value ?: continue // 获取当前服务类型
            when (serviceType) {
                "switch" -> { // 开关类型
                    for (property in service.properties) { // 遍历属性
                        val piid = property.iid // 获取当前属性iid
                        val propertyType =
                            MiotSpecService.parseUrn(property.type)?.value ?: continue // 获取当前特性类型
                        when (propertyType) {
                            "on" -> { // on 代表的一般是开关
                                manager.addView(
                                    binding.switchOutlet,
                                    propertyType,
                                    siid,
                                    piid,
                                    false
                                )
                                binding.switchOutlet.setOnStatusChangedListener {
                                    if (it) binding.deviceStatus.text =
                                        getString(
                                            R.string.device_opened
                                        ) else binding.deviceStatus.text = getString(
                                        R.string.device_closed
                                    )
                                }
                            }
                        }
                    }
                }
            }
            for (x in service.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when (type2) {
                    "temperature" -> {
                        manager.addView(binding.temperature, type2, siid, piid, 0)
                    }

                    "working-time" -> {
                        manager.addView(binding.workingTime, type2, siid, piid, 0)
                    }
                }
            }
        }
        for (i in miotServices) {
            val type = MiotSpecService.parseUrn(i.type)?.value ?: continue
            if (type != "power-consumption") continue
            val siid = i.iid
            for (x in i.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when (type2) {
                    "power-consumption" -> {
                        manager.addView(binding.powerConsumption, type2, siid, piid, 0)
                    }

                    "electric-current" -> {
                        manager.addView(binding.electricCurrent, type2, siid, piid, 0)
                    }

                    "voltage" -> {
                        manager.addView(binding.voltage, type2, siid, piid, 0)
                    }

                    "electric-power" -> {
                        manager.addView(binding.electricPower, type2, siid, piid, 0)
                    }
                }
            }
        }
        manager.init()
        manager.update()
        return binding.root
    }
}