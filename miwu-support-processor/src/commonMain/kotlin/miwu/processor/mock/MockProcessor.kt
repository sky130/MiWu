package miwu.processor.mock

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.CoroutineScope
import miwu.annotation.Mock
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.processor.MiwuProcessor
import kotlin.reflect.KClass

internal class MockProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : MiwuProcessor() {


    override fun onProcess(resolver: Resolver): List<KSAnnotated> {
        try {
            val mockMappings = collectMockMappings(resolver)
            generateMockRegistry(mockMappings)
        } catch (e: Exception) {
            logger.error("Failed to process mock annotations")
        }
        return emptyList()
    }

    private fun collectMockMappings(resolver: Resolver): Map<ClassName, ClassName> {
        val mockMappings = mutableMapOf<ClassName, ClassName>()

        resolver.getSymbolsWithAnnotation(Mock::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->
                try {
                    processMockDeclaration(declaration, mockMappings)
                } catch (e: Exception) {
                    logger.error(
                        "Failed to process mock declaration: ${declaration.qualifiedName?.asString()}",
                        declaration
                    )
                }
            }

        return mockMappings
    }

    private fun processMockDeclaration(
        declaration: KSClassDeclaration,
        mockMappings: MutableMap<ClassName, ClassName>
    ) {
        val mockClassName = declaration.toClassName()
        val wrappedClassName = extractWrappedClassName(declaration)

        if (wrappedClassName != null) {
            mockMappings[mockClassName] = wrappedClassName
            logger.info("Registered mock: $mockClassName -> $wrappedClassName")
        } else {
            logger.error(
                "Failed to extract wrapped class from @Mock annotation",
                declaration
            )
        }
    }

    private fun extractWrappedClassName(declaration: KSClassDeclaration): ClassName? {
        val mockAnnotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == MOCK_ANNOTATION_NAME }

        if (mockAnnotation == null) {
            logger.error("@Mock annotation not found", declaration)
            return null
        }

        val widgetArgument = mockAnnotation.arguments
            .firstOrNull { it.name?.asString() == MOCK_CLIENT_ARGUMENT_NAME }

        if (widgetArgument == null) {
            logger.error("'widget' argument not found in @Mock annotation", declaration)
            return null
        }

        val wrappedType = widgetArgument.value as? KSType
        if (wrappedType == null) {
            logger.error("Invalid 'widget' argument type in @Mock annotation", declaration)
            return null
        }

        return try {
            wrappedType.toClassName()
        } catch (e: Exception) {
            logger.error("Failed to convert wrapped type to ClassName", declaration)
            null
        }
    }

    private fun generateMockRegistry(mockMappings: Map<ClassName, ClassName>) {
        if (mockMappings.isEmpty()) {
            logger.info("No mock mappings found, skipping registry generation")
            return
        }

        val registryCodeBlock = createRegistryCodeBlock(mockMappings)
        val registryObject = createRegistryObject(registryCodeBlock)

        try {
            FileSpec.builder(PACKAGE_NAME, OBJECT_NAME)
                .addType(registryObject)
                .build()
                .writeTo(codeGenerator = codeGenerator, aggregating = true)

            logger.info("Generated MockRegistry with ${mockMappings.size} mappings")
        } catch (e: Exception) {
            logger.error("Failed to write MockRegistry file")
        }
    }

    private fun createRegistryCodeBlock(mockMappings: Map<ClassName, ClassName>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                mockMappings.entries.forEachIndexed { index, (mockClass, wrappedClass) ->
                    add("%T::class to ::%T", mockClass, wrappedClass)
                    if (index < mockMappings.size - 1) {
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
            ).addFunction(
                FunSpec.builder("createMockClient")
                    .addModifiers(KModifier.INLINE)
                    .addTypeVariable(TypeVariableName("reified T"))
                    .addParameter("mockScope", CoroutineScope::class)
                    .addParameter("specAtt", SpecAtt::class)
                    .addParameter("device", MiotDevice::class)
                    .returns(baseMockClient)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement(
                                "return registry[T::class]?.invoke(mockScope, specAtt, device) ?: %T(mockScope, specAtt, device)",
                                defaultMockClient
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }

    @Suppress("unused")
    private fun Sequence<KSAnnotation>.extractWidgetType(annotationName: String): ClassName? {
        return firstOrNull { it.shortName.asString() == annotationName }
            ?.arguments
            ?.firstOrNull { it.name?.asString() == MOCK_CLIENT_ARGUMENT_NAME }
            ?.value
            ?.let { it as? KSType }
            ?.toClassName()
    }

    companion object Companion {
        private const val PACKAGE_NAME = "miwu.support.generated.mock"
        private const val OBJECT_NAME = "MockRegistry"
        private const val MOCK_ANNOTATION_NAME = "Mock"
        private const val MOCK_CLIENT_ARGUMENT_NAME = "mockClient"

        private val baseMockClient = ClassName("miwu.mock.base", "BaseMockMiotDeviceClient")
        private val defaultMockClient = ClassName("miwu.mock", "DefaultMockMiotDeviceClient")

        private val REGISTRY_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                KClass::class.asTypeName().parameterizedBy(STAR),
                Function3::class.asTypeName().parameterizedBy(
                    CoroutineScope::class.asTypeName(),
                    SpecAtt::class.asTypeName(),
                    MiotDevice::class.asTypeName(),
                    baseMockClient
                )
            )
    }
}
