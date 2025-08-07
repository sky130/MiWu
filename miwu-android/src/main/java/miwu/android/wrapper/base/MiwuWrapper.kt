package miwu.android.wrapper.base

import android.content.Context
import android.view.View
import miwu.support.base.MiwuWidget

abstract class MiwuWrapper<T>(context: Context, widget: MiwuWidget<T>) : BaseMiwuWrapper<T>(context, widget) {

    open val onClickView: View get() = view

    override fun init() {
        super.init()
        onClickView.setOnClickListener {
            onClick()
        }
    }

    open fun onClick() {}

}