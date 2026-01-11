package miwu.processor.spec

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.runBlocking
import miwu.processor.spec.logic.SpecProvider
import java.util.Locale

class SpecProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {

    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (options["miwu.spec.enabled"] != "true") return emptyList()
        if (isProcessingOver) return emptyList()
        runBlocking {
            SPEC_TYPES.forEach { specType ->
                try {
                    handleSpecType(specType)
                } catch (e: Exception) {
                    logger.error("Failed to process spec type: $specType")
                }
            }
        }
        isProcessingOver = true
        return emptyList()
    }

    private suspend fun handleSpecType(specType: SpecType) {
        val specItems = fetchSpecItems(specType)
        if (specItems.isEmpty()) {
            logger.warn("No items found for spec type: ${specType.typeName}")
            return
        }
        val properties = createConstantProperties(specItems)
        val specObject = createSpecObject(specType.typeName, properties)
        generateSpecFile(specType.typeName, specObject)
    }

    private suspend fun fetchSpecItems(specType: SpecType): List<String> {
        return try {
            when (specType) {
                SpecType.DEVICE -> SpecProvider.service.getDevices()
                SpecType.SERVICE -> SpecProvider.service.getServices()
                SpecType.ACTION -> SpecProvider.service.getActions()
                SpecType.PROPERTY -> SpecProvider.service.getProperties()
            }.toNameList()
        } catch (e: Exception) {
            logger.error("Failed to fetch ${specType.typeName} items from SpecProvider")
            emptyList()
        }
    }

    private fun createConstantProperties(items: List<String>): List<PropertySpec> {
        return items.mapNotNull { name ->
            try {
                val propertyName = name.toConstantName()
                if (propertyName.isValidIdentifier()) {
                    PropertySpec.builder(propertyName, String::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", name)
                        .build()
                } else if ("_$propertyName".isValidIdentifier()) {
                    PropertySpec.builder("_$propertyName", String::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", name)
                        .build()
                } else if ("$propertyName$DELIMITER".isValidIdentifier()) {
                    null
                } else {
                    logger.warn("Skipping invalid property name: $propertyName (from: $name)")
                    null
                }
            } catch (e: Exception) {
                logger.warn("Failed to create property for name: $name")
                null
            }
        }
    }

    private fun createSpecObject(objectName: String, properties: List<PropertySpec>): TypeSpec {
        return TypeSpec.objectBuilder(objectName)
            .apply {
                properties.forEach { property ->
                    addProperty(property)
                }
            }
            .build()
    }

    private fun generateSpecFile(objectName: String, specObject: TypeSpec) {
        try {
            FileSpec.builder(PACKAGE_NAME, objectName)
                .addType(specObject)
                .build()
                .writeTo(codeGenerator = codeGenerator, aggregating = true)
        } catch (e: Exception) {
            logger.error("Failed to generate file for $objectName")
        }
    }

    private fun String.toConstantName(): String {
        return split(DELIMITER)
            .joinToString(EMPTY_STRING) { segment ->
                segment.capitalizeFirstChar()
            }
            .replace(DOT, UNDERSCORE)
    }

    private fun String.capitalizeFirstChar(): String {
        return replaceFirstChar { char ->
            if (char.isLowerCase()) {
                char.titlecase(Locale.getDefault())
            } else {
                char.toString()
            }
        }
    }

    private fun String.isValidIdentifier(): Boolean {
        return isNotEmpty() &&
                first().isJavaIdentifierStart() &&
                all { it.isJavaIdentifierPart() }
    }

    private enum class SpecType(val typeName: String) {
        DEVICE("Device"),
        SERVICE("Service"),
        ACTION("Action"),
        PROPERTY("Property");
    }

    companion object {
        private val SPEC_TYPES = SpecType.entries
        private const val PACKAGE_NAME = "miwu.spec"
        private const val DELIMITER = "-"
        private const val DOT = "."
        private const val UNDERSCORE = "_"
        private const val EMPTY_STRING = ""
    }
}
