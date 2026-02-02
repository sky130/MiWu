package miwu.support.api

interface Observer<T> {
    fun onUpdateValue(value: T) {}
    fun onUpdateValue(siid: Int, piid: Int, value: Any) {}
    fun onActionCallback(siid: Int, aiid: Int, output: Any?) {}
}