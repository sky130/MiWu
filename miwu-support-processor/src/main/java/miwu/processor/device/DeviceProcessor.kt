package miwu.processor.device

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
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
import miwu.annotation.Device

internal class DeviceProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : Processor {
    private var isProcessingOver = false


    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val deviceMap = mutableMapOf<String, ClassName>()
        val mapType = Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Class::class.asTypeName()
                .parameterizedBy(
                    STAR
                )
        )
        resolver.getSymbolsWithAnnotation(Device::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { declaration ->
                val annotation =
                    declaration.annotations.firstOrNull { it.shortName.asString() == "Device" }
                        ?: return@forEach logger.warn("@Device not found")
                val argument = annotation.arguments.firstOrNull { it.name?.asString() == "model" }
                    ?: return@forEach
                val model = argument.value as String
                deviceMap[model] = declaration.toClassName()
            }

        val objectName = "DeviceRegistry"
        val codeBlock = CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                deviceMap.entries.forEachIndexed { i, (model,className) ->
                    add("%S to %T::class.java, ", model, className)
                }
            }
            .unindent()
            .add("\n)")
            .build()
        val registry = TypeSpec.objectBuilder(objectName)
            .addProperty(
                PropertySpec.builder("registry", mapType).initializer(codeBlock).build()
            ).build()

        FileSpec.builder("miwu.widget.generated.device", objectName)
            .addType(registry)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
        isProcessingOver = true
        return emptyList()
    }
}

