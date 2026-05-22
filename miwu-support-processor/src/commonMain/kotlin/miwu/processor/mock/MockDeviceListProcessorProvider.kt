package miwu.processor.mock

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider

internal class MockDeviceListProcessorProvider : Provider {
    override fun create(environment: Environment) = MockDeviceListProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}
