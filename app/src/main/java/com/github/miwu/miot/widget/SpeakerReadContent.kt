package com.github.miwu.miot.widget

import android.content.Context
import kndroidx.extension.log
import com.github.miwu.databinding.MiotWidgetSpeakerReadContentBinding as Binding


class SpeakerReadContent(context: Context) : MiotBaseWidget<Binding>(context) {

    override fun init() {
        binding.button.setOnClickListener {
            getActionWithSiid("play-text").apply {
                binding.text.text.log.d()
                doAction(first, second.iid, isOut = false, binding.text.text.toString())
            }
        }
    }


    override fun onValueChange(value: Any) = Unit

}