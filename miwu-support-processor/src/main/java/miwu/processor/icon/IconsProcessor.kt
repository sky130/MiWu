package miwu.processor.icon

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.InputStream
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.KModifier

class IconsProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        val txtStream: InputStream = this::class.java.classLoader.getResourceAsStream("icons.txt")
            ?: run {
                logger.error("icons.txt not found in resources!")
                return emptyList()
            }
        val iconNames = txtStream.bufferedReader().readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { !it.startsWith("//") }
        val iconClass = ClassName("miwu.support.icon", "Icon")
        val noneIconClass = ClassName("miwu.icon", "NoneIcon")

        val mapToFun = FunSpec.builder("mapTo")
            .addModifiers(KModifier.PUBLIC)
            .returns(iconClass)
            .addParameter("name", String::class)
            .beginControlFlow("return when(name)")
            .apply {
                iconNames.forEach { name ->
                    addStatement(
                        "%S, %S -> %L",
                        name,
                        name.replace(Regex("([A-Z])"), "_$1").lowercase().trim('_'),
                        name
                    )
                }
                addStatement("else -> %T", noneIconClass)
            }
            .endControlFlow()
            .build()

        val iconInterface = TypeSpec.interfaceBuilder("Icons")
            .addModifiers(KModifier.PUBLIC)
            .apply {
                iconNames.forEach { name ->
                    addProperty(
                        PropertySpec.builder(name, iconClass)
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
                    )
                }
                addFunction(mapToFun)
            }
            .build()

        val fileSpec = FileSpec.builder("miwu.icon", "Icons")
            .addType(iconInterface)
            .build()

        codeGenerator.createNewFile(
            Dependencies(false),
            "miwu.icon",
            "Icons"
        ).writer().use { writer ->
            fileSpec.writeTo(writer)
        }
        isProcessingOver = true
        return emptyList()
    }
}
