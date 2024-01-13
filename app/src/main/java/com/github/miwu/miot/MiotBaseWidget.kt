@file:Suppress("PackageDirectoryMismatch")

package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.github.miwu.miot.MiotDeviceManager
import miot.kotlin.model.att.SpecAtt
import java.lang.reflect.ParameterizedType

sealed class MiotBaseWidget<VB : ViewBinding>(context: Context) : FrameLayout(context) {
    var piid: Int = -1
    var siid: Int = -1
    val binding: VB
    var property: SpecAtt.Service.Property? = null
    var action: SpecAtt.Service.Action? = null
    private lateinit var miotManager: MiotDeviceManager

    init {
        binding = createViewBinding()
    }

    open fun init(){}

    abstract fun onValueChange(value: Any) // 需要自己转换类型

    fun putValue(value: Any) {
        miotManager.putValue(value, siid, piid)
    }

    fun stopRefresh() = miotManager.stopRefresh()

    fun startRefresh() = miotManager.delay()

    fun setManager(manager: MiotDeviceManager) {
        if (!::miotManager.isInitialized) {
            miotManager = manager
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun View.createViewBinding(): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return inflate.invoke(null, LayoutInflater.from(context), this, true) as VB
    }

}