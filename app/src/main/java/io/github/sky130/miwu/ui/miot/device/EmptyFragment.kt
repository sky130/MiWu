package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.MainApplication
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.DeviceEmptyBinding
import io.github.sky130.miwu.logic.dao.UserDAO
import io.github.sky130.miwu.logic.network.MiotService
import io.github.sky130.miwu.logic.network.miot.UserService
import io.github.sky130.miwu.startActivity
import io.github.sky130.miwu.ui.MainActivity
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.util.GlideUtils
import io.github.sky130.miwu.util.TextUtils.toast
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class EmptyFragment : BaseFragment() {
    private lateinit var binding: DeviceEmptyBinding
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceEmptyBinding.inflate(inflater)
        loadImageAsync()
        return binding.root
    }

    fun loadImageAsync() {
        executor.execute {
            val url = MiotService.getModelIconUrl(getModel())
            // 更新UI需要切换到主线程
            runOnUiThread {
                if (url != null) {
                    GlideUtils.loadImg(url, binding.deviceImage)
                }
            }
        }
    }


}