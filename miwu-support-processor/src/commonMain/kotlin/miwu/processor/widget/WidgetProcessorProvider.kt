package miwu.processor.widget


import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider


internal class WidgetProcessorProvider : Provider {
    override fun create(environment: Environment) = WidgetProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}



