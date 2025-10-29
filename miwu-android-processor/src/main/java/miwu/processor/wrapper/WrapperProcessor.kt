package miwu.processor.wrapper

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import miwu.annotation.Wrapper

internal class WrapperProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {

    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()

        try {
            val wrapperMappings = collectWrapperMappings(resolver)
            generateWrapperRegistry(wrapperMappings)
        } catch (e: Exception) {
            logger.error("Failed to process wrapper annotations")
        }

        isProcessingOver = true
        return emptyList()
    }

    private fun collectWrapperMappings(resolver: Resolver): Map<ClassName, ClassName> {
        val wrapperMappings = mutableMapOf<ClassName, ClassName>()

        resolver.getSymbolsWithAnnotation(Wrapper::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->
                try {
                    processWrapperDeclaration(declaration, wrapperMappings)
                } catch (e: Exception) {
                    logger.error(
                        "Failed to process wrapper declaration: ${declaration.qualifiedName?.asString()}",
                        declaration
                    )
                }
            }

        return wrapperMappings
    }

    private fun processWrapperDeclaration(
        declaration: KSClassDeclaration,
        wrapperMappings: MutableMap<ClassName, ClassName>
    ) {
        val wrapperClassName = declaration.toClassName()
        val wrappedClassName = extractWrappedClassName(declaration)

        if (wrappedClassName != null) {
            wrapperMappings[wrapperClassName] = wrappedClassName
            logger.info("Registered wrapper: $wrapperClassName -> $wrappedClassName")
        } else {
            logger.error(
                "Failed to extract wrapped class from @Wrapper annotation",
                declaration
            )
        }
    }

    private fun extractWrappedClassName(declaration: KSClassDeclaration): ClassName? {
        val wrapperAnnotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == WRAPPER_ANNOTATION_NAME }

        if (wrapperAnnotation == null) {
            logger.error("@Wrapper annotation not found", declaration)
            return null
        }

        val widgetArgument = wrapperAnnotation.arguments
            .firstOrNull { it.name?.asString() == WIDGET_ARGUMENT_NAME }

        if (widgetArgument == null) {
            logger.error("'widget' argument not found in @Wrapper annotation", declaration)
            return null
        }

        val wrappedType = widgetArgument.value as? KSType
        if (wrappedType == null) {
            logger.error("Invalid 'widget' argument type in @Wrapper annotation", declaration)
            return null
        }

        return try {
            wrappedType.toClassName()
        } catch (e: Exception) {
            logger.error("Failed to convert wrapped type to ClassName", declaration)
            null
        }
    }

    private fun generateWrapperRegistry(wrapperMappings: Map<ClassName, ClassName>) {
        if (wrapperMappings.isEmpty()) {
            logger.info("No wrapper mappings found, skipping registry generation")
            return
        }

        val registryCodeBlock = createRegistryCodeBlock(wrapperMappings)
        val registryObject = createRegistryObject(registryCodeBlock)

        try {
            FileSpec.builder(PACKAGE_NAME, OBJECT_NAME)
                .addType(registryObject)
                .build()
                .writeTo(codeGenerator = codeGenerator, aggregating = true)

            logger.info("Generated WrapperRegistry with ${wrapperMappings.size} mappings")
        } catch (e: Exception) {
            logger.error("Failed to write WrapperRegistry file")
        }
    }

    private fun createRegistryCodeBlock(wrapperMappings: Map<ClassName, ClassName>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                wrapperMappings.entries.forEachIndexed { index, (wrapperClass, wrappedClass) ->
                    add("%T::class.java to %T::class.java", wrapperClass, wrappedClass)
                    if (index < wrapperMappings.size - 1) {
                        add(",\n")
                    } else {
                        add("\n")
                    }
                }
            }
            .unindent()
            .add(")")
            .build()
    }

    private fun createRegistryObject(codeBlock: CodeBlock): TypeSpec {
        return TypeSpec.objectBuilder(OBJECT_NAME)
            .addProperty(
                PropertySpec.builder("registry", REGISTRY_MAP_TYPE)
                    .initializer(codeBlock)
                    .build()
            )
            .build()
    }

    // 扩展函数用于提取注解信息（保留原有功能但未使用）
    @Suppress("unused")
    private fun Sequence<KSAnnotation>.extractWidgetType(annotationName: String): ClassName? {
        return firstOrNull { it.shortName.asString() == annotationName }
            ?.arguments
            ?.firstOrNull { it.name?.asString() == WIDGET_ARGUMENT_NAME }
            ?.value
            ?.let { it as? KSType }
            ?.toClassName()
    }

    companion object {
        private const val PACKAGE_NAME = "miwu.widget.generated.wrapper"
        private const val OBJECT_NAME = "WrapperRegistry"
        private const val WRAPPER_ANNOTATION_NAME = "Wrapper"
        private const val WIDGET_ARGUMENT_NAME = "widget"

        private val REGISTRY_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                Class::class.asTypeName().parameterizedBy(STAR),
                Class::class.asTypeName().parameterizedBy(STAR)
            )
    }
}
