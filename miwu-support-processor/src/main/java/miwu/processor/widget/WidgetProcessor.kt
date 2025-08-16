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
        val annotation = firstOrNull { it.shortName.asString() == name } ?: return emptyList()
        val valueList =
            annotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? List<*>
                ?: return emptyList()
        return valueList.map {
            it as String
        }
    }


    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val serviceMap = mutableMapOf<String, MutableMap<String, ClassName>>()
        val mapType = Map::class.asClassName()
            .parameterizedBy(
                String::class.asTypeName(),
                Map::class.asClassName().parameterizedBy(
                    String::class.asTypeName(),
                    Class::class.asTypeName().parameterizedBy(STAR)
                )
            )
        resolver.getSymbolsWithAnnotation(Widget::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { declaration ->
                val services = declaration.annotations.get("Service")
                val properties = declaration.annotations.get("Property")
                val actions = declaration.annotations.get("Action")

                if (services.isEmpty() || (properties.isEmpty() && actions.isEmpty())) return emptyList()
                services.forEach { service ->
                    properties.forEach { property ->
                        val map = serviceMap[service] ?: mutableMapOf()
                        map[property] = declaration.toClassName()
                        serviceMap[service] = map
                    }
                    actions.forEach { action ->
                        val map = serviceMap[service] ?: mutableMapOf()
                        map[action] = declaration.toClassName()
                        serviceMap[service] = map
                    }
                }
            }

        val objectName = "ServiceRegistry"
        val codeBlock = CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                serviceMap.entries.forEachIndexed { i, (service, properties) ->
                    add("%S to mapOf(", service)
                    properties.entries.forEachIndexed { j, (property, className) ->
                        add("%S to %T::class.java", property, className)
                        if (j != properties.size - 1) add(", ")
                    }
                    add(")")
                    if (i != serviceMap.size - 1) add(",\n") else add("\n")
                }
            }
            .unindent()
            .add(")")
            .build()
        val registry = TypeSpec.objectBuilder(objectName)
            .addProperty(
                PropertySpec.builder("registry", mapType).initializer(codeBlock).build()
            ).build()

        FileSpec.builder("miwu.widget.generated.widget", objectName)
            .addType(registry)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
        isProcessingOver = true
        return emptyList()
    }
}

