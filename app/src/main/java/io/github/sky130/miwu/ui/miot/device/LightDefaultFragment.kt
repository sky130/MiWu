package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.DeviceLightDefaultBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.miot.MiotService
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.ui.manager.MiWidgetManager
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils

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
        manager.notify(1500) // 1.5秒更新一次数据
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.cancelNotify()
    }


}