package miwu.processor.spec


import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider


internal class SpecProcessorProvider : Provider {
    override fun create(environment: Environment) = SpecProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}



