@file:Suppress("PropertyName")
package miwu.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ClassName
import com.google.devtools.ksp.processing.SymbolProcessor as Processor

abstract class MiwuProcessor : Processor {
    private var isProcessingOver = false


    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        isProcessingOver = true
        val processedSymbols = onProcess(resolver)
        return processedSymbols
    }

    abstract fun onProcess(resolver: Resolver): List<KSAnnotated>

    companion object {
        val MiwuDevice = ClassName("miwu.support", "MiwuDevice")

        val MiwuWidget = ClassName("miwu.support", "MiwuWidget")
    }
}