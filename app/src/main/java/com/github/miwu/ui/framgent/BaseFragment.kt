package com.github.miwu.ui.framgent

import androidx.fragment.app.Fragment
import com.github.miwu.logic.model.mi.MiDevice
import com.github.miwu.ui.MainActivity
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.ui.miot.DeviceUtils.startDeviceActivity
import com.github.miwu.widget.ViewExtra

open class BaseFragment : Fragment(), ViewExtra, RefreshListExtra {

    private val mainActivity: MainActivity
        get() = context as MainActivity

    fun startDeviceActivity(miDevice: MiDevice) = mainActivity.startDeviceActivity(
        miDevice.model,
        miDevice.did,
        miDevice.deviceName,
        miDevice.specType
    )

}