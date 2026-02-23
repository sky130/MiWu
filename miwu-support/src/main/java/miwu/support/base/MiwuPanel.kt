package miwu.support.base

import miwu.annotation.basic.Widget

abstract class MiwuPanel<T> : MiwuWidget<T>() {
    internal var createWrapper: (Class<out Widget>) -> Unit = {}

    open fun onCreateChild() = Unit

    inline fun <reified T : Widget> create(block: (MiwuWidget<*>) -> Unit) {
//        createWrapper
    }
}