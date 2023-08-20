package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils
import java.util.concurrent.Executors
import io.github.sky130.miwu.databinding.DeviceLemeshLightWy0c03Binding as Binding

class lemesh_ight_wy0c03() : BaseFragment() {
    private lateinit var binding: Binding
    private lateinit var did: String
    private val executor = Executors.newSingleThreadExecutor()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = Binding.inflate(layoutInflater)
        did = getDid()
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
        return binding.root
    }

}