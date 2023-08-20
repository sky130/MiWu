package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.DeviceLightDefaultBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.miot.MiotService
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils
import java.util.concurrent.Executors

class LightDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceLightDefaultBinding
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceLightDefaultBinding.inflate(inflater)
        executor.execute {
            val home = HomeDAO.getHome(HomeDAO.getHomeIndex()) // 获取家庭对象
            var url = ""
            home?.deviceList?.forEach { device ->
                if (device.model == getModel()) {
                    url = device.iconUrl
                }
            }
            // 更新UI需要切换到主线程
            runOnUiThread {
                if (url.isNotEmpty()) GlideUtils.loadImg(url, binding.deviceImage)
                if (binding.switchLight.getChecked()) binding.deviceStatus.text =
                    getString(R.string.device_opened) else binding.deviceStatus.text =
                    getString(R.string.device_closed)
            }
        }
        for (i in miotServices) {
            val urn = MiotSpecService.parseUrn(i.type) ?: continue
            val siid = i.iid
            if (urn.value == "light") {
                for (x in i.properties) {
                    val piid = x.iid
                    val urn2 = MiotSpecService.parseUrn(x.type) ?: continue
                    when (urn2.value) {
                        "on" -> {
                            binding.switchLight.apply {
                                setSiid(siid)
                                setPiid(piid)
                            }
                        }

                        "brightness" -> {
                            binding.brightness.apply {
                                setSiid(siid)
                                setPiid(piid)
                                x.valueRange ?: return@apply
                                getSeekBar().setMinProgress(x.valueRange[0].toInt())
                                getSeekBar().setMaxProgress(x.valueRange[1].toInt())
                            }
                        }

                        "color-temperature"->{
                            binding.colorTemperature.apply {
                                setSiid(siid)
                                setPiid(piid)
                                x.valueRange ?: return@apply
                                getSeekBar().setMinProgress(x.valueRange[0].toInt())
                                getSeekBar().setMaxProgress(x.valueRange[1].toInt())
                            }
                        }
                    }
                    x.type
                }
            }
        }
        return binding.root
    }

}