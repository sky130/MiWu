package miwu.miot

import miwu.miot.kmp.MiotClientImpl
import miwu.miot.kmp.MiotManagerImpl
import org.koin.dsl.module



val singleModule = module {
    single<MiotManager> { MiotManagerImpl() }
}

val normalModule = module {
    factory<MiotClient> { MiotClientImpl() }
}