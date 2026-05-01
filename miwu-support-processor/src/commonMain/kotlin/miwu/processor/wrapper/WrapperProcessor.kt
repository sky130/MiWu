package miwu.processor.wrapper

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import miwu.annotation.Wrapper
import miwu.processor.MiwuProcessor
import kotlin.reflect.KClass

internal class WrapperProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : MiwuProcessor() {

    override fun onProcess(resolver: Resolver): List<KSAnnotated> {
        try {
            val wrapperMappings = collectWrapperMappings(resolver)
            generateWrapperRegistry(wrapperMappings)
        } catch (e: Exception) {
            logger.error("Failed to process wrapper annotations")
        }
        return emptyList()
    }

    /**
     * 收集所有 @Wrapper 注解的映射信息
     *
     * @return Wrapper 信息列表，每个包含 wrapper 类名、被包装的 widget 类名、widget 泛型参数
     */
    private fun collectWrapperMappings(resolver: Resolver): List<WrapperInfo> {
        val wrapperMappings = mutableListOf<WrapperInfo>()

        resolver.getSymbolsWithAnnotation(Wrapper::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->
                try {
                    processWrapperDeclaration(declaration, wrapperMappings)
                } catch (e: Exception) {
                    logger.error(
                        "Failed to process wrapper declaration: ${declaration.qualifiedName?.asString()}, ${e.stackTraceToString()}",
                        declaration
                    )
                }
            }

        return wrapperMappings
    }

    private fun processWrapperDeclaration(
        declaration: KSClassDeclaration,
        wrapperMappings: MutableList<WrapperInfo>
    ) {
        val wrapperClassName = declaration.toClassName()
        val wrappedClassName = extractWrappedClassName(declaration)

        if (wrappedClassName == null) {
            logger.error(
                "Failed to extract wrapped class from @Wrapper annotation",
                declaration
            )
            return
        }

        val typeArg = extractWidgetTypeArg(wrappedClassName, declaration)
        if (typeArg == null) {
            logger.error(
                "Failed to extract MiwuWidget type argument from $wrappedClassName",
                declaration
            )
            return
        }

        wrapperMappings += WrapperInfo(wrapperClassName, wrappedClassName, typeArg)
        logger.info("Registered wrapper: $wrapperClassName -> $wrappedClassName<${typeArg.simpleName}>")
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

    /**
     * 从 Wrapper 类的构造函数中提取 MiwuWidget<T> 的泛型参数 T
     *
     * 解析构造函数的第二个参数类型（即 widget: MiwuWidget<T>），提取 T。
     *
     * @param widgetClassName widget 类的 ClassName（未使用，保留用于日志）
     * @param declaration wrapper 类的声明
     * @return MiwuWidget 的泛型参数 ClassName，提取失败返回 null
     */
    private fun extractWidgetTypeArg(
        widgetClassName: ClassName,
        declaration: KSClassDeclaration
    ): ClassName? {
        // 获取构造函数的第二个参数（widget: MiwuWidget<T>）
        val constructor = declaration.primaryConstructor ?: return null
        val params = constructor.parameters
        if (params.size < 2) return null

        val widgetParamType = params[1].type.resolve()
        val typeArgs = widgetParamType.arguments
        if (typeArgs.isEmpty()) return null

        return typeArgs.first().type?.resolve()?.toClassName()
    }

    private fun generateWrapperRegistry(wrapperMappings: List<WrapperInfo>) {
        if (wrapperMappings.isEmpty()) {
            logger.info("No wrapper mappings found, skipping registry generation")
            return
        }

        val registryCodeBlock = createRegistryCodeBlock(wrapperMappings)
        val constructorCodeBlock = createConstructorCodeBlock(wrapperMappings)
        val registryObject = createRegistryObject(registryCodeBlock, constructorCodeBlock)

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

    private fun createRegistryCodeBlock(wrapperMappings: List<WrapperInfo>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                wrapperMappings.forEachIndexed { index, info ->
                    add("%T::class to %T::class", info.wrappedClass, info.wrapperClass)
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

    private fun createConstructorCodeBlock(wrapperMappings: List<WrapperInfo>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                wrapperMappings.forEachIndexed { index, info ->
                    add(
                        "%T::class to { ctx, widget -> %T(ctx, widget as %T) }",
                        info.wrappedClass,
                        info.wrapperClass,
                        MiwuWidget.parameterizedBy(info.typeArg)
                    )
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

    private fun createRegistryObject(
        registryCodeBlock: CodeBlock,
        constructorCodeBlock: CodeBlock
    ): TypeSpec {
        return TypeSpec.objectBuilder(OBJECT_NAME)
            .addProperty(
                PropertySpec.builder("registry", REGISTRY_MAP_TYPE)
                    .initializer(registryCodeBlock)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("constructor", CONSTRUCTOR_MAP_TYPE)
                    .initializer(constructorCodeBlock)
                    .build()
            )
            .addFunction(
                FunSpec.builder("create")
                    .addParameter("context", CONTEXT_CLASS_NAME)
                    .addParameter("widget", MiwuWidget.parameterizedBy(STAR))
                    .returns(VIEW_MIWU_WRAPPER.parameterizedBy(STAR).copy(nullable = true))
                    .addStatement("return constructor[widget::class]?.invoke(context, widget)")
                    .build()
            )
            .build()
    }

    private data class WrapperInfo(
        val wrapperClass: ClassName,
        val wrappedClass: ClassName,
        val typeArg: ClassName
    )

    companion object {
        private const val PACKAGE_NAME = "miwu.support.generated.wrapper"
        private const val OBJECT_NAME = "WrapperRegistry"
        private const val WRAPPER_ANNOTATION_NAME = "Wrapper"
        private const val WIDGET_ARGUMENT_NAME = "widget"

        private val VIEW_MIWU_WRAPPER = ClassName("miwu.android.wrapper.base", "ViewMiwuWrapper")
        private val CONTEXT_CLASS_NAME = ClassName("android.content", "Context")

        // registry: Map<KClass<out MiwuWidget<*>>, KClass<out ViewMiwuWrapper<*>>>
        private val REGISTRY_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                KClass::class.asTypeName()
                    .parameterizedBy(
                        WildcardTypeName.producerOf(MiwuWidget.parameterizedBy(STAR))
                    ),
                KClass::class.asTypeName()
                    .parameterizedBy(
                        WildcardTypeName.producerOf(VIEW_MIWU_WRAPPER.parameterizedBy(STAR))
                    )
            )

        // constructor: Map<KClass<out MiwuWidget<*>>, Function2<Context, MiwuWidget<*>, ViewMiwuWrapper<*>>>
        private val CONSTRUCTOR_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                KClass::class.asTypeName()
                    .parameterizedBy(
                        WildcardTypeName.producerOf(MiwuWidget.parameterizedBy(STAR))
                    ),
                Function2::class.asTypeName()
                    .parameterizedBy(
                        CONTEXT_CLASS_NAME,
                        MiwuWidget.parameterizedBy(STAR),
                        VIEW_MIWU_WRAPPER.parameterizedBy(STAR)
                    )
            )
    }
}
