package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.DeviceRouterDefaultBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.miot.MiotService
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.ui.manager.MiWidgetManager
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils

class RouterDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    private lateinit var binding: DeviceRouterDefaultBinding
    private val manager = MiWidgetManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DeviceRouterDefaultBinding.inflate(inflater)
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
            if (type != "router") continue
            val siid = i.iid
            for (x in i.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when (type2) {
                    "upload-speed" -> {
                        manager.addView(binding.upload, type2, siid, piid, 0)
                    }

                    "download-speed" -> {
                        manager.addView(binding.download, type2, siid, piid, 0)
                    }
                }
            }
        }
        manager.init()
        manager.update()
        return binding.root
    }

}