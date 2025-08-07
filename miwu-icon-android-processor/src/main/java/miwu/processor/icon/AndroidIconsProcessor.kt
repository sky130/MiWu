package miwu.processor.icon


import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*

class AndroidIconsProcessor(
    private val options: Map<String, String>,
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    private var isProcessingOver = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()

        val iconsInterface = resolver.getClassDeclarationByName("miwu.icon.Icons") ?: return emptyList()
        val properties = iconsInterface.getAllProperties().toList()

        val packageName = "miwu.android.icon.generated.icon"
        val fileName = "AndroidIcons"

        val superInterface = ClassName("miwu.icon", "Icons")
        val androidIconClass = ClassName("miwu.android.icon", "AndroidIcon")
        val rDrawable = ClassName("miwu.android", "R").nestedClass("drawable")

        val typeSpec = TypeSpec.objectBuilder(fileName)
            .addSuperinterface(superInterface)
            .apply {
                properties.forEach { prop ->
                    val propName = prop.simpleName.asString()
                    addProperty(
                        PropertySpec.builder(propName, androidIconClass)
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer(
                                "%T(%T.ic_${propName.camelToSnake()})",
                                androidIconClass,
                                rDrawable,
                            )
                            .build()
                    )
                }
            }
            .build()

        val fileSpec = FileSpec.builder(packageName, fileName)
            .addType(typeSpec)
            .addImport("miwu.android.icon", "AndroidIcon")
            .addImport("miwu.android", "R")
            .build()

        // 4. 写入文件
        codeGenerator.createNewFile(
            Dependencies(false),
            packageName,
            fileName
        ).use { stream ->
            stream.writer().use { writer ->
                fileSpec.writeTo(writer)
            }
        }

        isProcessingOver = true
        return emptyList()
    }

    fun String.camelToSnake(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(Regex("([A-Z])([A-Z][a-z])"), "$1_$2")
            .lowercase()
}
