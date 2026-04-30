package miwu.annotation

import miwu.annotation.basic.MockClient
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(val mockClient: KClass<out MockClient>)
