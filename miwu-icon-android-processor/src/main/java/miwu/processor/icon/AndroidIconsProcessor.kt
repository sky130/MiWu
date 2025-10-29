package miwu.processor.icon

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class AndroidIconsProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        try {
            val iconProperties = extractIconProperties(resolver) ?: return emptyList()
            generateAndroidIconsImplementation(iconProperties)
        } catch (e: Exception) {
            logger.error("Failed to process Android icons")
        }
        isProcessingOver = true
        return emptyList()
    }

    private fun extractIconProperties(resolver: Resolver): List<IconProperty>? {
        val iconsInterface = resolver.getClassDeclarationByName(ICONS_INTERFACE_NAME)
        if (iconsInterface == null) {
            logger.error("Icons interface not found: $ICONS_INTERFACE_NAME")
            return null
        }
        val properties = iconsInterface.getAllProperties().toList()
        if (properties.isEmpty()) {
            logger.warn("No properties found in Icons interface")
            return emptyList()
        }
        return properties.mapNotNull { property ->
            val propertyName = property.simpleName.asString()
            if (propertyName.isValidIconName()) {
                IconProperty(propertyName)
            } else {
                logger.warn("Skipping invalid icon property name: $propertyName")
                null
            }
        }
    }

    private fun generateAndroidIconsImplementation(iconProperties: List<IconProperty>) {
        val androidIconsObject = createAndroidIconsObject(iconProperties)
        val fileSpec = createFileSpec(androidIconsObject)
        try {
            fileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
            logger.info("Generated AndroidIcons with ${iconProperties.size} properties")
        } catch (e: Exception) {
            logger.error("Failed to write AndroidIcons file")
        }
    }

    private fun createAndroidIconsObject(iconProperties: List<IconProperty>): TypeSpec {
        return TypeSpec.objectBuilder(OBJECT_NAME)
            .addSuperinterface(SUPER_INTERFACE)
            .apply {
                iconProperties.forEach { iconProperty ->
                    addProperty(createIconProperty(iconProperty))
                }
            }
            .build()
    }

    private fun createIconProperty(iconProperty: IconProperty): PropertySpec {
        val drawableResourceName = iconProperty.name.camelToSnakeCase()

        return PropertySpec.builder(iconProperty.name, ANDROID_ICON_CLASS)
            .addModifiers(KModifier.OVERRIDE)
            .initializer(
                "%T(%T.ic_%L)",
                ANDROID_ICON_CLASS,
                R_DRAWABLE_CLASS,
                drawableResourceName
            )
            .build()
    }

    private fun createFileSpec(androidIconsObject: TypeSpec): FileSpec {
        return FileSpec.builder(PACKAGE_NAME, OBJECT_NAME)
            .addType(androidIconsObject)
            .addImport(ANDROID_ICON_PACKAGE, ANDROID_ICON_SIMPLE_NAME)
            .addImport(R_PACKAGE, R_SIMPLE_NAME)
            .build()
    }

    private fun String.camelToSnakeCase(): String {
        return this
            .replace(CAMEL_TO_SNAKE_REGEX_1, "$1_$2")
            .replace(CAMEL_TO_SNAKE_REGEX_2, "$1_$2")
            .lowercase()
    }

    private fun String.isValidIconName(): Boolean {
        return isNotEmpty() &&
                first().isJavaIdentifierStart() &&
                all { it.isJavaIdentifierPart() }
    }

    private data class IconProperty(val name: String)

    companion object {
        private const val PACKAGE_NAME = "miwu.android.icon.generated.icon"
        private const val OBJECT_NAME = "AndroidIcons"
        private const val ICONS_INTERFACE_NAME = "miwu.icon.Icons"

        private val SUPER_INTERFACE = ClassName("miwu.icon", "Icons")
        private val ANDROID_ICON_CLASS = ClassName("miwu.android.icon", "AndroidIcon")
        private val R_DRAWABLE_CLASS = ClassName("miwu.android", "R").nestedClass("drawable")

        private const val ANDROID_ICON_PACKAGE = "miwu.android.icon"
        private const val ANDROID_ICON_SIMPLE_NAME = "AndroidIcon"
        private const val R_PACKAGE = "miwu.android"
        private const val R_SIMPLE_NAME = "R"

        private val CAMEL_TO_SNAKE_REGEX_1 = Regex("([a-z])([A-Z])")
        private val CAMEL_TO_SNAKE_REGEX_2 = Regex("([A-Z])([A-Z][a-z])")
    }
}
