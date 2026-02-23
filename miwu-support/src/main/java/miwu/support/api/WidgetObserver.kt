package miwu.support.api

interface WidgetObserver<T> {
    fun onUpdateValue(value: T) = Unit
    fun onUpdateValue(siid: Int, piid: Int, value: Any) = Unit
    fun onActionCallback(siid: Int, aiid: Int, output: Any?) = Unit
}