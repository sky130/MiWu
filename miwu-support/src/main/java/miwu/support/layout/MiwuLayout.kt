package miwu.support.layout

import miwu.support.base.MiwuWidget


typealias WidgetList = ArrayList<MiwuWidget<*>>

data class MiwuLayout(
    val header: WidgetList = arrayListOf(),
    val subHeader: WidgetList = arrayListOf(),
    val body: WidgetList = arrayListOf(),
    val subFooter: WidgetList = arrayListOf(),
    val footer: WidgetList = arrayListOf(),
    val unknown: WidgetList = arrayListOf(),
){
    fun clear(){
        header.clear()
        subHeader.clear()
        body.clear()
        subFooter.clear()
        footer.clear()
        unknown.clear()
    }

    fun Header(block: (MiwuWidget<*>) -> Unit){
        header.forEach { block(it) }
    }

    fun SubHeader(block: (MiwuWidget<*>) -> Unit){
        subHeader.forEach { block(it) }
    }

    fun Body(block: (MiwuWidget<*>) -> Unit){
        body.forEach { block(it) }
    }

    fun SubFooter(block: (MiwuWidget<*>) -> Unit){
        subFooter.forEach { block(it) }
    }

    fun Footer(block: (MiwuWidget<*>) -> Unit){
        footer.forEach { block(it) }
    }

    fun Unknown(block: (MiwuWidget<*>) -> Unit){
        unknown.forEach { block(it) }
    }
}

