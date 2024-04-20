package com.github.miwu.logic.repository.model

import android.util.ArrayMap
import com.github.miwu.logic.repository.DeviceArrayList
import com.github.miwu.logic.repository.DeviceList
import miot.kotlin.model.miot.MiotDevices

data class SmartHome(val name: String) {

    val deviceList = DeviceArrayList()
    val textList = ArrayList<String>()

    sealed class Base

    data class Home(val value: SmartHome) : Base()

    data class Device(val value: MiotDevices.Result.Device) : Base()

    companion object {

        fun MiotDevices.Result.Device.toBase() = Device(this)

        fun SmartHome.toBase() = Home(this)

    }

}