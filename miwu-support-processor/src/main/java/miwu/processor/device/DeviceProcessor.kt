package miwu.processor.device

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
        val deviceMap = collectDeviceClasses(resolver)
        if (deviceMap.isNotEmpty()) {
            generateDeviceRegistry(deviceMap)
        }
        isProcessingOver = true
        return emptyList()
    }

    private fun collectDeviceClasses(resolver: Resolver): Map<String, ClassName> {
        val deviceMap = mutableMapOf<String, ClassName>()
        resolver.getSymbolsWithAnnotation(Device::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->
                val model = extractDeviceModel(declaration)
                if (model != null) {
                    deviceMap[model] = declaration.toClassName()
                } else {
                    logger.warn("Failed to extract model from @Device annotation in ${declaration.qualifiedName?.asString()}")
                }
            }
        return deviceMap
    }

    private fun extractDeviceModel(declaration: KSClassDeclaration): String? {
        val deviceAnnotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == "Device" }
        if (deviceAnnotation == null) {
            logger.warn("@Device annotation not found on ${declaration.qualifiedName?.asString()}")
            return null
        }
        val modelArgument = deviceAnnotation.arguments
            .firstOrNull { it.name?.asString() == "model" }
        if (modelArgument == null) {
            logger.warn("'model' argument not found in @Device annotation on ${declaration.qualifiedName?.asString()}")
            return null
        }
        return modelArgument.value as? String
    }

    private fun generateDeviceRegistry(deviceMap: Map<String, ClassName>) {
        val mapType = createMapType()
        val registryCodeBlock = createRegistryCodeBlock(deviceMap)
        val registry = TypeSpec.objectBuilder(OBJECT_NAME)
            .addProperty(
                PropertySpec.builder("registry", mapType)
                    .initializer(registryCodeBlock)
                    .build()
            )
            .build()
        FileSpec.builder(PACKAGE_NAME, OBJECT_NAME)
            .addType(registry)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun createMapType(): ParameterizedTypeName {
        return Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Class::class.asTypeName().parameterizedBy(STAR)
        )
    }

    private fun createRegistryCodeBlock(deviceMap: Map<String, ClassName>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                deviceMap.forEach { (model, className) ->
                    add("%S to %T::class.java,\n", model, className)
                }
            }
            .unindent()
            .add(")")
            .build()
    }

    companion object {
        private const val OBJECT_NAME = "DeviceRegistry"
        private const val PACKAGE_NAME = "miwu.widget.generated.device"
    }
}
