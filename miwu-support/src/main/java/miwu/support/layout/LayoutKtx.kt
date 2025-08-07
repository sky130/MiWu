package miwu.support.layout

fun on(layout: MiwuLayout,block:MiwuLayout.() -> Unit){
    layout.block()
}
