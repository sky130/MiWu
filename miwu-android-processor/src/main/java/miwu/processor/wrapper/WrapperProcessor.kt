package miwu.processor.wrapper

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
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

import miwu.annotation.Wrapper
import kotlin.reflect.KType

internal class WrapperProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {
    private var isProcessingOver = false

    fun Sequence<KSAnnotation>.get(name: String): KType? {
        val annotation = firstOrNull { it.shortName.asString() == name } ?: return null
        return annotation.arguments.firstOrNull { it.name?.asString() == "widget" }?.value as? KType
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val classMap = mutableMapOf<ClassName, ClassName>()
        val mapType = Map::class.asClassName()
            .parameterizedBy(
                Class::class.asTypeName().parameterizedBy(STAR),
                Class::class.asTypeName().parameterizedBy(STAR)
            )
        resolver
            .getSymbolsWithAnnotation(Wrapper::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->

                val wrapperClassName = declaration.toClassName()
                val wrappedClassAnnotation = declaration.annotations
                    .firstOrNull { it.shortName.asString() == "Wrapper" }
                val wrappedClassArg = wrappedClassAnnotation
                    ?.arguments
                    ?.firstOrNull { it.name?.asString() == "widget" }
                    ?.value as? KSType

                val wrappedClassName = wrappedClassArg?.toClassName()
                if (wrappedClassName != null) {
                    classMap[wrapperClassName] = wrappedClassName
                }
            }


        val objectName = "WrapperRegistry"
        val codeBlock = CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                classMap.entries.forEachIndexed { i, (widget, wrapper) ->
                    add("%T::class.java to %T::class.java", wrapper, widget)
                    if (i != classMap.size - 1) add(",\n") else add("\n")
                }
            }
            .unindent()
            .add(")")
            .build()
        val registry = TypeSpec.objectBuilder(objectName)
            .addProperty(
                PropertySpec.builder("registry", mapType).initializer(codeBlock).build()
            ).build()

        FileSpec.builder("miwu.widget.generated.wrapper", objectName)
            .addType(registry)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
        isProcessingOver = true
        return emptyList()
    }
}

