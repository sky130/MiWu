package com.github.miwu.miot.widget

import android.content.Context
import kndroidx.extension.log
import miot.kotlin.utils.parseUrn
import com.github.miwu.databinding.MiotWidgetVacuumButtonBarBinding as Binding


class VacuumButtonBar(context: Context) : MiotBaseWidget<Binding>(context) {
    private var isSweep = false

    override fun init() {
        binding.first.setOnClickListener {
            if (isSweep) {
                doAction("stop-sweeping")
            } else {
                doAction("start-sweep")
            }
        }

        binding.second.setOnClickListener {
            doAction("start-charge")
        }
    }

    private fun doAction(name: String) {
        for (i in actions) {
            if (i.second.type.parseUrn().name == name) {
                doAction(i.first, i.second.iid)
                return
            }
        }
    }

    override fun onValueChange(value: Any) {
        value as Number

        isSweep = when (value.toInt()) {
            0, 4, 8 -> {
                false
            }

            else -> {
                true
            }
        }
    }

}