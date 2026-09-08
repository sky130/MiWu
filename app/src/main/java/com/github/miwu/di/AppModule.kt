package com.github.miwu.di

import miwu.miot.Provider
import miwu.miot.common.MiotApiKoinModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module
@ComponentScan("com.github.miwu")
class AppModule