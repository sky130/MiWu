package miwu.annotation

import miwu.annotation.basic.Widget
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Widgets(vararg val widgets: KClass<out Widget>)
