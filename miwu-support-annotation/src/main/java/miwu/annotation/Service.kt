package miwu.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Service(
    vararg val name: String
)

