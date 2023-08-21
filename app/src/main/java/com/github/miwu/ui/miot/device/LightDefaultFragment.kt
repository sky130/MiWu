package com.github.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.databinding.DeviceLightDefaultBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.logic.model.miot.MiotService
import com.github.miwu.logic.network.MiotSpecService
import com.github.miwu.ui.manager.MiWidgetManager
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.util.GlideUtils

class LightDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceLightDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceLightDefaultBinding.inflate(inflater)
        manager.setDid(getDid())
        var url = ""
        HomeDAO.getHome(HomeDAO.getHomeIndex())?.deviceList?.forEach { device ->
            if (device.model == getModel()) {
                url = device.iconUrl
            }
        }
        if (url.isNotEmpty()) GlideUtils.loadImg(url, binding.deviceImage)
        for (i in miotServices) {
            val urn = MiotSpecService.parseUrn(i.type) ?: continue
            val siid = i.iid
            if (urn.value == "light") {
                for (x in i.properties) {
                    val piid = x.iid
                    val urn2 = MiotSpecService.parseUrn(x.type) ?: continue
                    when (urn2.value) {
                        "on" -> {
                            manager.addView(binding.switchLight, urn2.value, siid, piid, false)
                            binding.switchLight.setOnStatusChangedListener(true) {
                                if (isAdded) if (it) binding.deviceStatus.text =
                                    getString(
                                        R.string.device_opened
                                    ) else binding.deviceStatus.text = getString(
                                    R.string.device_closed
                                )
                            }
                        }

                        "brightness" -> {
                            x.valueRange ?: continue
                            manager.addView(
                                binding.brightness,
                                urn2.value,
                                siid,
                                piid,
                                0f,
                                x.valueRange[1].toInt(),
                                x.valueRange[0].toInt(),
                            )
                        }

                        "color-temperature" -> {
                            x.valueRange ?: continue
                            manager.addView(
                                binding.colorTemperature,
                                urn2.value,
                                siid,
                                piid,
                                0f,
                                x.valueRange[1].toInt(),
                                x.valueRange[0].toInt(),
                            )
                        }
                    }
                    x.type
                }
            }
        }
        manager.init()
        manager.update()
        manager.notify(1000) // 1秒更新一次数据
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.cancelNotify()
    }


}