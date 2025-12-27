package miwu.support.base

import miwu.annotation.basic.Widget

abstract class MiwuLayout<T> : MiwuWidget<T>() {
    internal var createWidget: (Class<out Widget>) -> Unit = {}


    open fun onCreateChild() {

    }

    inline fun <reified T : Widget> create(block: (MiwuWidget<*>) -> Unit) {

    }
}