package com.github.miwu.widget.miot

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

sealed interface MiotWidgetImpl<VB : ViewBinding> {

    val piid: Int
    val siid: Int
    val putValue: (Any, Int, Int) -> Unit
    var binding: VB

    fun onValueChange(value: Any) // 需要自己转换类型

    @Suppress("UNCHECKED_CAST")
    fun View.createViewBinding(): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return inflate.invoke(null, LayoutInflater.from(context)) as VB
    }

}