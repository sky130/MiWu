package miwu.processor.spec

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.runBlocking
import miwu.processor.spec.logic.SpecProvider
import java.util.Locale.getDefault
import com.google.devtools.ksp.processing.SymbolProcessor as Processor

class SpecProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : Processor {
    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        runBlocking {
            listOf("Device", "Service", "Action", "Property").forEach {
                handle(it)
            }
        }
        isProcessingOver = true
        return emptyList()
    }

    suspend fun handle(type: String) {
        val list = when (type) {
            "Device" -> SpecProvider.service.getDevices()
            "Service" -> SpecProvider.service.getServices()
            "Action" -> SpecProvider.service.getActions()
            "Property" -> SpecProvider.service.getProperties()
            else -> return
        }.toNameList()
        val objectName = type
        val codeBlock = TypeSpec.objectBuilder(objectName)
            .apply {
                list.forEach { name ->
                    val typeName = name.split("-").joinToString("") { it.up() }.replace(".", "_")
                    addProperty(
                        PropertySpec.builder(typeName, String::class)
                            .addModifiers(KModifier.CONST)
                            .initializer("%S", name)
                            .build()
                    )
                }
            }
            .build()
        FileSpec.builder("miwu.spec", objectName)
            .addType(codeBlock)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    fun String.up() =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }

}