package miwu.miot

import org.koin.dsl.module



val singleModule = module {
    single<MiotManager> { MiotManagerImpl() }
}

val normalModule = module {
    factory<MiotClient> { MiotClientImpl() }
}