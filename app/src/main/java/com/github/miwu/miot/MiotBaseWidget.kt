@file:Suppress("PackageDirectoryMismatch")
package com.github.miwu.miot.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.github.miwu.miot.MiotDeviceManager
import java.lang.reflect.ParameterizedType

sealed class MiotBaseWidget<VB : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attributeSet, defStyleAttr) {

    var piid: Int = -1
    var siid: Int = -1
    val binding by lazy { createViewBinding() }
    private lateinit var miotManager:MiotDeviceManager

    abstract fun onValueChange(value: Any) // 需要自己转换类型

    fun putValue(value: Any) {
        miotManager.putValue(value, siid, piid)
    }

    fun setManager(manager: MiotDeviceManager){
        if (::miotManager.isInitialized){
            miotManager = manager
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun View.createViewBinding(): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return inflate.invoke(null, LayoutInflater.from(context)) as VB
    }

}