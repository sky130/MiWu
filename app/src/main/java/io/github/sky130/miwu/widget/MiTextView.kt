package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiTextViewBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
import kotlin.concurrent.thread

class MiTextView(context: Context, attr: AttributeSet) : LinearLayout(context, attr), ViewExtra {

    private var binding: MiTextViewBinding
    private val unit: String
    private var block: ((Int) -> Unit)? = null
    private lateinit var mActivity: DeviceActivity
    private lateinit var did: String
    private var siid: Int
    private var piid: Int

    init {
        binding = MiTextViewBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiTextView).apply {
            binding.title.text = getString(R.styleable.MiTextView_title).toString()
            unit = getString(R.styleable.MiTextView_unit).toString()
            siid = getInt(R.styleable.MiTextView_siid, 0)
            piid = getInt(R.styleable.MiTextView_piid, 0)
            setText("0")
            recycle()
            if (!isInEditMode) {
                mActivity = context as DeviceActivity
                did = mActivity.did
                reset()
            }
            reset()
        }
    }

    fun setSiid(siid: Int) {
        this.siid = siid
        if (siid == 0 || piid == 0) return
        reset()
    }

    fun setPiid(piid: Int) {
        this.piid = piid
        if (siid == 0 || piid == 0) return
        reset()
    }

    private fun reset() {
        if (siid == 0 || piid == 0) return
        thread {
            val text =
                DeviceService.getDeviceATT(did, siid, piid)?.value.toString()
            runOnUiThread {
                setText(text)
            }
        }
        setText(binding.value.text)
    }

    @SuppressLint("SetTextI18n")
    fun setText(text: CharSequence) {
        binding.value.text = "$text$unit"
    }
}