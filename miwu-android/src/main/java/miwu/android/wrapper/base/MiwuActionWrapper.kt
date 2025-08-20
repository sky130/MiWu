package miwu.android.wrapper.base

import android.content.Context
import android.view.View
import miwu.support.base.MiwuWidget

abstract class MiwuActionWrapper(context: Context, widget: MiwuWidget<Unit>) : BaseMiwuWrapper<Unit>(context, widget) {

    open val onClickView: View get() = view

    override fun init() {
        super.init()
        onClickView.setOnClickListener {
            onClick()
        }
    }

    open fun onClick() {}

    override fun onUpdateValue(siid: Int, piid: Int, value: Any) {
        propertyListenerList[siid to piid]?.invoke(value)
    }

    override fun onUpdateValue(value: Unit) = Unit

}