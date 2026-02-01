package miwu.processor.icon

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.File
import java.io.InputStream

class IconsProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (options["miwu.icon.enabled"] == "false") return emptyList()
        val filePath = options["miwu.icon.filePath"] ?: return emptyList()
        if (isProcessingOver) return emptyList()
        val iconNames = loadIconNames(filePath) ?: return emptyList()
        val processedIcons = processIconNames(iconNames)
        generateIconsInterface(processedIcons)
        isProcessingOver = true
        return emptyList()
    }

    private fun loadIconNames(filePath: String): List<String>? {
        return File(filePath).apply {
            if (!this.isFile) {
                logger.error("$filePath not found in resources!")
                return null
            }
        }.inputStream().use { stream ->
            stream.bufferedReader().readLines()
                .asSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith(COMMENT_PREFIX) }
                .map { it.replace(" ", "") }
                .toList()
        }
    }

    private fun processIconNames(iconNames: List<String>): ProcessedIcons {
        val properties = mutableListOf<IconProperty>()
        val mappings = mutableListOf<IconMapping>()

        iconNames.forEach { line ->
            if (line.startsWith(HASH_PREFIX)) return@forEach
            if (SPLIT_SYMBOL in line) {
                val (originalName, iconName) = line.split(SPLIT_SYMBOL, limit = 2)
                mappings.add(createIconMapping(originalName.trim(), iconName.trim()))
            } else {
                properties.add(IconProperty(line))
                mappings.add(createIconMapping(line, line))
            }
        }

        return ProcessedIcons(properties, mappings)
    }

    private fun createIconMapping(name: String, iconName: String): IconMapping {
        return IconMapping(
            originalName = name,
            snakeCaseName = name.toSnakeCase(),
            kebabCaseName = name.toKebabCase(),
            iconName = iconName
        )
    }

    private fun generateIconsInterface(processedIcons: ProcessedIcons) {
        val iconClass = ClassName(ICON_PACKAGE, "Icon")
        val noneIconClass = ClassName(GENERATED_PACKAGE, "NoneIcon")

        val mapToFunction = createMapToFunction(processedIcons.mappings, iconClass, noneIconClass)
        val iconInterface = createIconInterface(processedIcons.properties, iconClass, mapToFunction)

        FileSpec.builder(GENERATED_PACKAGE, INTERFACE_NAME)
            .addType(iconInterface)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = false)
    }

    private fun createMapToFunction(
        mappings: List<IconMapping>,
        iconClass: ClassName,
        noneIconClass: ClassName
    ): FunSpec {
        return FunSpec.builder("mapTo")
            .addModifiers(KModifier.PUBLIC)
            .returns(iconClass)
            .addParameter("name", String::class)
            .beginControlFlow("return when(name)")
            .apply {
                mappings.forEach { mapping ->
                    addStatement(
                        "%S, %S, %S -> %L",
                        mapping.originalName,
                        mapping.snakeCaseName,
                        mapping.kebabCaseName,
                        mapping.iconName
                    )
                }
                addStatement("else -> %T", noneIconClass)
            }
            .endControlFlow()
            .build()
    }

    private fun createIconInterface(
        properties: List<IconProperty>,
        iconClass: ClassName,
        mapToFunction: FunSpec
    ): TypeSpec {
        return TypeSpec.interfaceBuilder(INTERFACE_NAME)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                properties.forEach { property ->
                    addProperty(
                        PropertySpec.builder(property.name, iconClass)
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
                    )
                }
                addFunction(mapToFunction)
            }
            .build()
    }

    private fun String.toSnakeCase(): String {
        return replace(Regex("([A-Z])"), "_$1")
            .lowercase()
            .trim('_')
    }

    private fun String.toKebabCase(): String {
        return replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()
            .trim('-')
    }

    private data class IconProperty(val name: String)

    private data class IconMapping(
        val originalName: String,
        val snakeCaseName: String,
        val kebabCaseName: String,
        val iconName: String
    )

    private data class ProcessedIcons(
        val properties: List<IconProperty>,
        val mappings: List<IconMapping>
    )

    companion object {
        // private const val ICONS_FILE_NAME = "icons.txt"
        private const val COMMENT_PREFIX = "//"
        private const val HASH_PREFIX = "#"
        private const val SPLIT_SYMBOL = "->"
        private const val INTERFACE_NAME = "Icons"
        private const val GENERATED_PACKAGE = "miwu.icon"
        private const val ICON_PACKAGE = "miwu.support.icon"
    }
}
