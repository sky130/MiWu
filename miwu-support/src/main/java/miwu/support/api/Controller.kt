package miwu.support.api

interface Controller {
    fun onUpdateValue(siid: Int, piid: Int, value: Any) {}
    fun doAction(siid: Int, aiid: Int, vararg input: Any) {}
    fun onActionCallback(siid: Int, aiid: Int, output: Any) {}
}