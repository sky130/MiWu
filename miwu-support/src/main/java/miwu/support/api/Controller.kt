package miwu.support.api

interface Controller {
    fun onUpdateValue(siid: Int, piid: Int, value: Any) {}
    fun doAction(siid: Int, aiid: Int, input: Any? = null) {}
}