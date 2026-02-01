package miwu.processor.icon


import miwu.processor.device.DeviceProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider


internal class IconsProcessorProvider : Provider {
    override fun create(environment: Environment) = IconsProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}



