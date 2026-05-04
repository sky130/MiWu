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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.CoroutineScope
import miwu.annotation.Mock
import miwu.miot.model.spec.SpecAtt
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

    private fun collectMockMappings(resolver: Resolver): Map<String, ClassName> {
        val mockMappings = mutableMapOf<String, ClassName>()

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
        mockMappings: MutableMap<String, ClassName>
    ) {
        val deviceName = extractDeviceName(declaration)
        val mockClassName = extractMockClientClassName(declaration)

        if (deviceName != null && mockClassName != null) {
            mockMappings[deviceName] = mockClassName
            logger.info("Registered mock: $deviceName -> $mockClassName")
        } else {
            logger.error(
                "Failed to extract wrapped class from @Mock annotation",
                declaration
            )
        }
    }

    private fun extractDeviceName(declaration: KSClassDeclaration): String? {
        val deviceAnnotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == DEVICE_ANNOTATION_NAME }
            ?: return null

        val nameArgument = deviceAnnotation.arguments
            .firstOrNull { it.name?.asString() == "model" }
            ?: return null

        return nameArgument.value as? String
    }

    private fun extractMockClientClassName(declaration: KSClassDeclaration): ClassName? {
        val mockAnnotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == MOCK_ANNOTATION_NAME }

        if (mockAnnotation == null) {
            logger.error("@Mock annotation not found", declaration)
            return null
        }

        val widgetArgument = mockAnnotation.arguments
            .firstOrNull { it.name?.asString() == MOCK_CLIENT_ARGUMENT_NAME }

        if (widgetArgument == null) {
            logger.error("'client' argument not found in @Mock annotation", declaration)
            return null
        }

        val mockClient = widgetArgument.value as? KSType
        if (mockClient == null) {
            logger.error("Invalid 'client' argument type in @Mock annotation", declaration)
            return null
        }

        return try {
            mockClient.toClassName()
        } catch (e: Exception) {
            logger.error("Failed to convert wrapped type to ClassName", declaration)
            null
        }
    }

    private fun generateMockRegistry(mockMappings: Map<String, ClassName>) {
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

    private fun createRegistryCodeBlock(mockMappings: Map<String, ClassName>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                mockMappings.entries.forEach { (deviceName, mockClientClass) ->
                    add("%S to ::%T,", deviceName, mockClientClass)
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
            .addFunction(
                FunSpec.builder("createMockClient")
                    .addParameter("deviceType", STRING)
                    .addParameter("mockScope", CoroutineScope::class)
                    .addParameter("specAtt", SpecAtt::class)
                    .addParameter("device", MiotDevice::class)
                    .returns(BaseMockClient)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement(
                                "return registry[deviceType]?.invoke(mockScope, specAtt, device) ?: %T(mockScope, specAtt, device)",
                                DefaultMockClient
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
        private const val DEVICE_ANNOTATION_NAME = "Device"
        private const val MOCK_CLIENT_ARGUMENT_NAME = "client"
        private val MiwuDeviceKClass =
            KClass::class.asTypeName()
                .parameterizedBy(
                    WildcardTypeName.producerOf(
                        ClassName("miwu.support", "MiwuDevice")
                    )
                )
        private val BaseMockClient =
            ClassName("miwu.support.mock.base", "BaseMockMiotDeviceClient")
        private val DefaultMockClient =
            ClassName("miwu.support.mock", "DefaultMockMiotDeviceClient")

        private val REGISTRY_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                String::class.asTypeName(),
                Function3::class.asTypeName().parameterizedBy(
                    CoroutineScope::class.asTypeName(),
                    SpecAtt::class.asTypeName(),
                    MiotDevice::class.asTypeName(),
                    BaseMockClient
                )
            )
    }
}
