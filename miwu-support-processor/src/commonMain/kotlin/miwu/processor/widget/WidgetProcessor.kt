package miwu.processor.widget

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor as Processor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import miwu.annotation.Widget

internal class WidgetProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {

    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val widgetMappings = collectWidgetMappings(resolver)
        generateRegistries(widgetMappings)
        isProcessingOver = true
        return emptyList()
    }

    private fun collectWidgetMappings(resolver: Resolver): WidgetMappings {
        val propertyMappings = mutableMapOf<ServiceItem, ClassName>()
        val actionMappings = mutableMapOf<ServiceItem, ClassName>()
        resolver.getSymbolsWithAnnotation(Widget::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration ->
                try {
                    processWidgetDeclaration(declaration, propertyMappings, actionMappings)
                } catch (e: Exception) {
                    logger.error("Failed to process widget: ${declaration.qualifiedName?.asString()}", declaration)
                }
            }
        return WidgetMappings(propertyMappings, actionMappings)
    }

    private fun processWidgetDeclaration(
        declaration: KSClassDeclaration,
        propertyMappings: MutableMap<ServiceItem, ClassName>,
        actionMappings: MutableMap<ServiceItem, ClassName>
    ) {
        val annotations = declaration.annotations
        val services = annotations.extractStringList(ANNOTATION_SERVICE)
        val properties = annotations.extractStringList(ANNOTATION_PROPERTY)
        val actions = annotations.extractStringList(ANNOTATION_ACTION)
        val bindings = annotations.extractBindings(ANNOTATION_BIND)
        if (services.isEmpty() && properties.isEmpty() && actions.isEmpty() && bindings.isEmpty()) {
            return
        }
        val className = declaration.toClassName()
        processBindings(bindings, className, propertyMappings, actionMappings)
        processLegacyAnnotations(services, properties, actions, className, propertyMappings, actionMappings, declaration)
    }

    private fun processBindings(
        bindings: List<BindingInfo>,
        className: ClassName,
        propertyMappings: MutableMap<ServiceItem, ClassName>,
        actionMappings: MutableMap<ServiceItem, ClassName>
    ) {
        bindings.forEach { binding ->
            val serviceItem = ServiceItem(binding.service, binding.item)
            when (binding.type) {
                BindingType.PROPERTY -> propertyMappings[serviceItem] = className
                BindingType.ACTION -> actionMappings[serviceItem] = className
                BindingType.UNKNOWN -> {
                    logger.warn("Unknown binding type for service: ${binding.service}, item: ${binding.item}")
                }
            }
        }
    }

    private fun processLegacyAnnotations(
        services: List<String>,
        properties: List<String>,
        actions: List<String>,
        className: ClassName,
        propertyMappings: MutableMap<ServiceItem, ClassName>,
        actionMappings: MutableMap<ServiceItem, ClassName>,
        declaration: KSClassDeclaration
    ) {
        if (services.isEmpty()) return
        try {
            services.forEachIndexed { index, service ->
                if (index < properties.size && properties[index].isNotBlank()) {
                    propertyMappings[ServiceItem(service, properties[index])] = className
                }
                if (index < actions.size && actions[index].isNotBlank()) {
                    actionMappings[ServiceItem(service, actions[index])] = className
                }
            }
        } catch (e: Exception) {
            logger.error(
                "Service, Property, and Action annotations must have matching indices in ${className.canonicalName}",
                declaration
            )
        }
    }

    private fun generateRegistries(mappings: WidgetMappings) {
        generateRegistry(PROPERTY_REGISTRY_NAME, mappings.propertyMappings)
        generateRegistry(ACTION_REGISTRY_NAME, mappings.actionMappings)
    }

    private fun generateRegistry(objectName: String, classMap: Map<ServiceItem, ClassName>) {
        if (classMap.isEmpty()) {
            logger.info("No mappings found for $objectName, skipping generation")
            return
        }

        val registryCodeBlock = createRegistryCodeBlock(classMap)
        val registryObject = createRegistryObject(objectName, registryCodeBlock)

        FileSpec.builder(PACKAGE_NAME, objectName)
            .addType(registryObject)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun createRegistryCodeBlock(classMap: Map<ServiceItem, ClassName>): CodeBlock {
        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                classMap.forEach { (serviceItem, className) ->
                    add("(%S to %S) to %T::class.java,\n", serviceItem.service, serviceItem.item, className)
                }
            }
            .unindent()
            .add(")")
            .build()
    }

    private fun createRegistryObject(objectName: String, codeBlock: CodeBlock): TypeSpec {
        return TypeSpec.objectBuilder(objectName)
            .addProperty(
                PropertySpec.builder("registry", REGISTRY_MAP_TYPE)
                    .initializer(codeBlock)
                    .build()
            )
            .build()
    }

    private fun Sequence<KSAnnotation>.extractStringList(annotationName: String): List<String> {
        return filter { it.shortName.asString() == annotationName }
            .flatMap { annotation ->
                annotation.arguments
                    .filter { it.name?.asString() == "name" }
                    .mapNotNull { it.value as? List<*> }
                    .flatten()
                    .filterIsInstance<String>()
            }
            .toList()
    }

    private fun Sequence<KSAnnotation>.extractBindings(annotationName: String): List<BindingInfo> {
        return filter { it.shortName.asString() == annotationName }
            .mapNotNull { annotation ->
                try {
                    val service = annotation.getArgumentValue("service") as? String ?: return@mapNotNull null
                    val item = annotation.getArgumentValue("item") as? String ?: return@mapNotNull null
                    val typeName = annotation.annotationType.resolve().arguments
                        .firstOrNull()?.type?.resolve()?.declaration?.qualifiedName?.asString()
                        ?: return@mapNotNull null

                    val bindingType = when (typeName) {
                        PROPERTY_ANNOTATION_QUALIFIED_NAME -> BindingType.PROPERTY
                        ACTION_ANNOTATION_QUALIFIED_NAME -> BindingType.ACTION
                        else -> BindingType.UNKNOWN
                    }

                    BindingInfo(service, item, bindingType)
                } catch (e: Exception) {
                    logger.warn("Failed to extract binding info from annotation", annotation)
                    null
                }
            }
            .toList()
    }

    private fun KSAnnotation.getArgumentValue(name: String): Any? {
        return arguments.firstOrNull { it.name?.asString() == name }?.value
    }

    private data class ServiceItem(val service: String, val item: String)
    private data class BindingInfo(val service: String, val item: String, val type: BindingType)
    private data class WidgetMappings(
        val propertyMappings: Map<ServiceItem, ClassName>,
        val actionMappings: Map<ServiceItem, ClassName>
    )
    private enum class BindingType {
        PROPERTY, ACTION, UNKNOWN
    }
    companion object {
        private const val PACKAGE_NAME = "miwu.widget.generated.widget"
        private const val PROPERTY_REGISTRY_NAME = "PropertyRegistry"
        private const val ACTION_REGISTRY_NAME = "ActionRegistry"

        private const val ANNOTATION_SERVICE = "Service"
        private const val ANNOTATION_PROPERTY = "Property"
        private const val ANNOTATION_ACTION = "Action"
        private const val ANNOTATION_BIND = "Bind"

        private const val PROPERTY_ANNOTATION_QUALIFIED_NAME = "miwu.annotation.Property"
        private const val ACTION_ANNOTATION_QUALIFIED_NAME = "miwu.annotation.Action"

        private val REGISTRY_MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(
                Pair::class.asClassName()
                    .parameterizedBy(String::class.asTypeName(), String::class.asTypeName()),
                Class::class.asTypeName().parameterizedBy(STAR)
            )
    }
}
