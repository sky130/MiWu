package com.github.miwu.ui.device

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.appScope
import com.github.miwu.MainApplication.Companion.gson
import com.github.miwu.databinding.ActivityDeviceBinding
import com.github.miwu.logic.database.model.MiwuDevice.Companion.toMiwu
import com.github.miwu.logic.repository.DeviceRepository
import com.github.miwu.miot.manager.MiotDeviceManager
import com.github.miwu.miot.manager.MiotQuickManager
import com.github.miwu.miot.device.DeviceType
import com.github.miwu.miot.initSpecAttFun
import com.github.miwu.viewmodel.DeviceViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.log
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.MiotManager
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.utils.parseUrn
import java.io.File

class DeviceActivity : ViewActivityX<ActivityDeviceBinding, DeviceViewModel>() {
    private lateinit var device: MiotDevices.Result.Device
    private val layout by lazy { binding.miotWidgetLayout }
    private val manager by lazy { MiotDeviceManager(device, layout) }
    private val urn by lazy { device.specType?.parseUrn() }
    private val mode by lazy { urn?.name }
    private var deviceType: DeviceType? = null

    override fun beforeSetContent() {
        device = gson.fromJson(
            intent.getStringExtra("device"), MiotDevices.Result.Device::class.java
        )
    }


    fun onAddButtonClick() {
        if (deviceType == null) {
            "设备暂不支持".toast()
        } else if (deviceType!!.isQuickActionable) {
            MiotQuickManager.addQuick(deviceType!!.getQuick()!!)
            "添加成功".toast()
        } else {
            "设备没有快捷操作".toast()
        }
    }

    fun onStarButtonClick() {
        appScope.launch {
            DeviceRepository.flow.take(1).collectLatest {
                DeviceRepository.replaceList(ArrayList(it).apply {
                    sortBy { it.index }
                    forEach {
                        if (it.did == device.did) {
                            withContext(Dispatchers.Main) {
                                "设备已添加".toast()
                            }
                            return@collectLatest
                        }
                    }
                    add(device.toMiwu())
                })
            }
        }
    }

    override fun init() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            device.specType?.also {
                File(this@DeviceActivity.cacheDir.absolutePath + "/" + it.hashCode()).let { file ->
                    if (file.isFile) {
                        val att = gson.fromJson(file.readText(), SpecAtt::class.java)
                        withContext(Dispatchers.Main) {
                            initSpecAtt(att)
                        }
                    } else {
                        val att = MiotManager.getSpecAttWithLanguage(it)
                        att?.let { at ->
                            file.writeText(gson.toJson(at))
                            withContext(Dispatchers.Main) {
                                initSpecAtt(at)
                            }
                        }
                    }
                }

            }.let {

            }
        }
    }

    private fun initSpecAtt(att: SpecAtt) {
        mode ?: return // TODO
        deviceType = initSpecAttFun(device, mode!!, att, layout, manager)
        manager.post(1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.destroy()
    }

    companion object {
        fun Context.startDeviceActivity(device: MiotDevices.Result.Device) {
            start<DeviceActivity> {
                putExtra("device", gson.toJson(device))
            }
        }
    }

}
