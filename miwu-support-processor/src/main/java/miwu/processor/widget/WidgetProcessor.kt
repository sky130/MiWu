package miwu.processor.widget

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

import miwu.annotation.Widget

internal class WidgetProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {
    private var isProcessingOver = false

    fun Sequence<KSAnnotation>.get(name: String): List<String> {
        val annotation = filter { it.shortName.asString() == name }.toList()
        if (annotation.isEmpty()) return emptyList()
        val valueList =
            buildList {
                annotation.forEach {
                    val arg =
                        it.arguments.firstOrNull { arg -> arg.name?.asString() == "name" }?.value
                    if (arg is List<*>) addAll(arg)
                }
            }
        return valueList.map {
            it as String
        }
    }

    fun Sequence<KSAnnotation>.getBind(name: String): List<Pair<String, Pair<String, String>>> {
        val annotation = filter { it.shortName.asString() == name }.toList()
        if (annotation.isEmpty()) return emptyList()
        val valueList =
            buildList {
                annotation.forEach {
                    fun get(name: String) =
                        it.arguments.firstOrNull { arg -> arg.name?.asString() == name }?.value as String

                    val typeName =
                        it.annotationType.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration?.qualifiedName?.asString()
                            ?: ""
                    add(get("service") to (get("item") to typeName))
                }
            }
        return valueList
    }

    private val mapType = Map::class.asClassName()
        .parameterizedBy(
            Pair::class
                .asClassName()
                .parameterizedBy(
                    String::class.asTypeName(), String::class.asTypeName()
                ),
            Class::class.asTypeName().parameterizedBy(STAR)
        )

//    private val mapType = Map::class.asClassName()
//        .parameterizedBy(
//            String::class.asTypeName(),
//            Map::class.asClassName()
//                .parameterizedBy(
//                    String::class.asTypeName(),
//                    Class::class.asTypeName().parameterizedBy(STAR)
//                )
//        )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val propertyClassMap = mutableMapOf<Pair<String, String>, ClassName>()
        val actionClassMap = mutableMapOf<Pair<String, String>, ClassName>()

        resolver.getSymbolsWithAnnotation(Widget::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { declaration ->
                val services = declaration.annotations.get("Service")
                val properties = declaration.annotations.get("Property")
                val actions = declaration.annotations.get("Action")
                val bind = declaration.annotations.getBind("Bind")
                if (services.isEmpty() && properties.isEmpty() && actions.isEmpty() && bind.isEmpty()) return@forEach
                val className = declaration.toClassName()

                bind.forEach { (service, pair) ->
                    val (item, type) = pair
                    when (type) {
                        "miwu.annotation.Property" -> {
                            propertyClassMap[service to item] = className
                        }

                        "miwu.annotation.Action" -> {
                            actionClassMap[service to item] = className
                        }
                    }
                }
                runCatching {
                    services.forEachIndexed { index, service ->
                        val property = properties[index]
                        val action = properties[index]

                        if (property.isNotBlank()) {
                            propertyClassMap[service to property] = className
                        }
                        if (action.isNotBlank()) {
                            actionClassMap[service to action] = className
                        }
                    }
                }.onFailure {
                    throw IllegalStateException(
                        "Please check ${className.canonicalName} whether `Service` and the corresponding `Property` or `Action` are correctly implemented.",
                        it
                    )
                }
                // services.forEach { service ->
                //     properties.forEach { property ->
                //         val map = serviceMap[service] ?: mutableMapOf()
                //         map[property] = declaration.toClassName()
                //         serviceMap[service] = map
                //     }
                //     actions.forEach { action ->
                //         val map = serviceMap[service] ?: mutableMapOf()
                //         map[action] = declaration.toClassName()
                //         serviceMap[service] = map
                //     }
                // }
            }

        handle("PropertyRegistry", propertyClassMap)
        handle("ActionRegistry", actionClassMap)
        isProcessingOver = true
        return emptyList()
    }

    fun handle(objectName: String, classMap: Map<Pair<String, String>, ClassName>) {
        // val objectName = "PropertyRegistry"
        val codeBlock = CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                classMap.forEach { (service, item), className ->
                    add("(%S to %S) to %T::class.java,\n", service, item, className)
                }
            }
            .unindent()
            .add(")")
            .build()
        val registry = TypeSpec.objectBuilder(objectName)
            .addProperty(
                PropertySpec
                    .builder("registry", mapType)
                    .initializer(codeBlock).build()
            ).build()

        FileSpec.builder("miwu.widget.generated.widget", objectName)
            .addType(registry)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }
}

