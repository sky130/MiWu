package io.github.sky130.miwu.ui.framgent

import androidx.fragment.app.Fragment
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.ui.MainActivity
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.ui.miot.DeviceUtils.startDeviceActivity
import io.github.sky130.miwu.widget.ViewExtra

open class BaseFragment : Fragment() , ViewExtra {

    private val mainActivity: MainActivity
        get() = context as MainActivity

    fun startDeviceActivity(miDevice: MiDevice) = mainActivity.startDeviceActivity(
        miDevice.model,
        miDevice.did,
        miDevice.deviceName,
        miDevice.specType
    )

}