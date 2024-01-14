package com.github.miwu.miot

import android.view.ViewGroup
import com.github.miwu.miot.device.Airer
import com.github.miwu.miot.device.Light
import com.github.miwu.miot.device.Vacuum
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices

// 未来考虑换成注解
fun initSpecAttFun(device: MiotDevices.Result.Device,mode: String, att: SpecAtt, layout: ViewGroup, manager: MiotDeviceManager) =
    when (mode) {
        "light" -> {
            Light(device,layout, manager).onLayout(att)
        }

        "airer" -> {
            Airer(device,layout, manager).onLayout(att)
        }

        "vacuum" -> {
            Vacuum(device,layout, manager).onLayout(att)
        }

        else -> {
            null
        }
    }
