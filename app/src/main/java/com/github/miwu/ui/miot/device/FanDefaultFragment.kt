package com.github.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.databinding.DeviceFanDefaultBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.logic.model.miot.MiotService
import com.github.miwu.logic.network.MiotSpecService
import com.github.miwu.ui.manager.MiWidgetManager
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.util.GlideUtils
import com.github.miwu.util.TextUtils.log

/**
 * @author OCD
 * @date 2023/08/28 16:29
 * Description:
 */
class FanDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceFanDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceFanDefaultBinding.inflate(inflater)
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
            val urn = MiotSpecService.parseUrn(i.type) ?: continue
            val siid = i.iid
            when (urn.value) {
                "fan" -> {
                    i.properties.forEach { it ->
                        var onPiid = 0
                        val piid = it.iid
                        val urn2 = MiotSpecService.parseUrn(it.type) ?: return@forEach
                        "${it.valueRange},text".log()
                        when (urn2.value) {
                            "on" -> {
                                onPiid = piid
                                manager.addView(binding.switchFan, urn2.value, siid, piid, false)
                                binding.switchFan.setOnStatusChangedListener(true) {
                                    if (it) binding.deviceStatus.text =
                                        MainApplication.context.getString(
                                            R.string.device_opened
                                        ) else binding.deviceStatus.text =
                                        MainApplication.context.getString(
                                            R.string.device_closed
                                        )
                                }
                            }

                            "fan-level" -> {
                                val list = it.valueList ?: return@forEach
                                onPiid.toString().log()
                                manager.addView(
                                    binding.fan,
                                    urn2.value,
                                    siid,
                                    piid,
                                    1
                                )
                                binding.fan.setDatas(list, urn2.value)
                            }

                            "mode" -> {
                                val list = it.valueList ?: return@forEach
                                onPiid.toString().log()
                                manager.addView(
                                    binding.mode,
                                    urn2.value,
                                    siid,
                                    piid,
                                    1
                                )
                                binding.mode.setDatas(list, urn2.value)
                            }
                        }
                    }
                }

                "environment" -> {
                    i.properties.forEach {
                        val environment = MiotSpecService.parseUrn(it.type)?.value ?: return@forEach
                        val piid = it.iid
                        when (environment) {
                            "temperature" -> {
                                manager.addView(binding.temperature, environment, siid, piid, 0)
                            }

                            "relative-humidity" -> {
                                manager.addView(
                                    binding.relativeHumidity, environment, siid, piid, 0
                                )
                            }
                        }
                    }
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