package miwu.android.wrapper.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import miwu.android.icon.AndroidIcon
import miwu.icon.NoneIcon
import miwu.support.base.MiwuWidget
import miwu.support.icon.Icon

abstract class ViewMiwuWrapper<T>(val context: Context, widget: MiwuWidget<T>) :
    miwu.support.base.MiwuWrapper<T>(widget) {
    abstract val view: View

    fun View.onClick(block: View.() -> Unit) {
        setOnClickListener(block)
    }

    fun ImageView.setIcon(icon: Icon) {
        when (icon) {
            is AndroidIcon -> {
                setImageResource(icon.resId)
            }

            is NoneIcon -> {
                setImageDrawable(null)
            }
        }
    }

    protected inline fun <reified VB : ViewBinding> viewBinding(crossinline inflate: (LayoutInflater) -> VB) =
        lazy { inflate(LayoutInflater.from(context)) }

    @Suppress("UNCHECKED_CAST")
    @Deprecated(
        "avoid unchecked cast",
        replaceWith = ReplaceWith("viewBinding(inflate)")
    )
    protected inline fun <reified VB : ViewBinding> viewBinding() =
        lazy {
            (VB::class.java.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
            ).invoke(null, LayoutInflater.from(context)) as VB)
        }

}