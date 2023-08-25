package com.github.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.databinding.DeviceThSensorDefaultBinding
import com.github.miwu.databinding.DeviceVacuumDefaultBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.logic.model.miot.MiotService
import com.github.miwu.logic.network.DeviceService
import com.github.miwu.logic.network.MiotSpecService
import com.github.miwu.ui.manager.MiWidgetManager
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.util.GlideUtils

// 全程Temperature Humidity Sensor
class VacuumDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceVacuumDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceVacuumDefaultBinding.inflate(inflater)
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
        for (i in miotServices) {
            val type = MiotSpecService.parseUrn(i.type)?.value ?: continue
            when (type) {
                "vacuum" -> {
                    val siid = i.iid
                    for (x in i.properties) {
                        val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                        val piid = x.iid
                        when (type2) {
                            "status" -> {
                                manager.addNotifyBlock("status") {
                                    val value = (DeviceService.getDeviceATT(
                                        getDid(),
                                        siid,
                                        piid
                                    )?.value as Number).toInt()
                                    x.valueList ?: return@addNotifyBlock
                                    setDeviceTitle(x.valueList[value].description)
                                }
                            }
                        }
                    }
                    i.actions ?: continue
                    for (x in i.actions) {
                        val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                        val aiid = x.iid
                        when (type2) {
                            "start-sweep" -> {
                                manager.addView(binding.sweep, type2, siid, aiid)
                            }
                        }
                    }
                }

                "battery" -> {
                    val siid = i.iid
                    i.actions ?: continue
                    for (x in i.actions) {
                        val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                        val aiid = x.iid
                        when (type2) {
                            "start-charge" -> {
                                manager.addView(binding.charge, type2, siid, aiid)
                            }
                        }
                    }
                }
            }
        }
        manager.init()
        manager.update()
        manager.notify(1000)
        return binding.root
    }

    fun setDeviceTitle(str: String) {
        runOnUiThread {
            binding.deviceStatus.text = str
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.cancelNotify()
    }
}