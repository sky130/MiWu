package miwu.processor.icon


import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider


internal class AndroidIconsProcessorProvider : Provider {
    override fun create(environment: Environment) = AndroidIconsProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}



