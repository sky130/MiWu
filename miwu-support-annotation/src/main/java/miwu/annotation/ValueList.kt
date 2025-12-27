package miwu.annotation

import kotlin.reflect.KClass

/**
 * @param pointTo If a property have two types, you could use this param to enable another property.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueList(val pointTo: KClass<*> = ValueList::class)
