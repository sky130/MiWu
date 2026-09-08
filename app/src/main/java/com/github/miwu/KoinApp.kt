package com.github.miwu

import com.github.miwu.di.AppModule
import com.github.miwu.di.MiotModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication

@KoinApplication(
    modules = [
        AppModule::class,
        MiotModule::class
    ]
)
class KoinApp