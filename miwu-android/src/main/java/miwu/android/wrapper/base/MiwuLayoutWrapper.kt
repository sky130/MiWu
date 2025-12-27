package miwu.android.wrapper.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import miwu.android.databinding.MiotLayoutWrapperBinding
import miwu.support.base.MiwuWidget

abstract class MiwuLayoutWrapper<T>(context: Context, widget: MiwuWidget<T>) :
    ViewMiwuWrapper<T>(context, widget) {
    private val binding by viewBinding(MiotLayoutWrapperBinding::inflate)
    private val onClickView: View get() = view
    override val view get() = binding.root

    protected inline fun <reified VB : ViewBinding> view(
        crossinline inflate: (LayoutInflater) -> VB,
        block: VB.() -> Unit
    ): VB = inflate(LayoutInflater.from(context)).apply {
        block()
        add(root)
    }

    fun add(content: View) {
        binding.root.add(content)
    }
}