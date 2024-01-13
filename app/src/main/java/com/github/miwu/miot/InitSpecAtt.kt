package com.github.miwu.miot

import android.view.ViewGroup
import com.github.miwu.miot.device.Airer
import com.github.miwu.miot.device.Light
import miot.kotlin.model.att.SpecAtt

// 未来考虑换成注解
fun initSpecAttFun(mode: String, att: SpecAtt, layout: ViewGroup, manager: MiotDeviceManager) {
    when (mode) {
        "light" -> {
            Light(layout, manager).onLayout(att)
        }

        "airer" -> {
            Airer(layout, manager).onLayout(att)
        }
    }
}