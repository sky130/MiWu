package io.github.sky130.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.MiSwitchCardBinding
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.ui.DeviceActivity
import kotlin.concurrent.thread


@SuppressLint("ClickableViewAccessibility")
class MiSwitchCard(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr),
    ViewExtra {
    private var binding: MiSwitchCardBinding
    private var block: ((Boolean) -> Unit)? = null
    private lateinit var mActivity: DeviceActivity
    private lateinit var did: String
    private var siid: Int
    private var piid: Int
    private var isChecked = false


    fun setOnProgressChangedListener(block: (Boolean) -> Unit) {
        this.block = block
    }

    init {
        binding = MiSwitchCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiSwitchCard).apply {
            siid = getInt(R.styleable.MiSwitchCard_siid, 0)
            piid = getInt(R.styleable.MiSwitchCard_piid, 0)
            recycle()
        }
        if (!isInEditMode) {
            mActivity = context as DeviceActivity
            did = mActivity.did
            setOnProgressChangedListener {
                thread {
                    DeviceService.setDeviceATT(did, siid, piid, it)
                }
            }
            setOnClickListener {
                setChecked(!isChecked)
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
            val boolean =
                DeviceService.getDeviceATT(did, siid, piid)?.getValue(false)!!
            runOnUiThread {
                setChecked(boolean)
            }
        }
    }

    fun setChecked(boolean: Boolean) {
        this.isChecked = boolean
        block?.invoke(boolean)
        if (boolean) {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_on)
            binding.title.text = "开启"
        } else {
            binding.miSwitchButton.setBackgroundResource(R.drawable.bg_swicth_button_off)
            binding.title.text = "关闭"
        }
    }


}