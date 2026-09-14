package miwu.miot.kmp

import miwu.dispatchers.DispatcherModule
import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.impl.client.MiotDeviceClientImpl
import miwu.miot.kmp.impl.client.MiotHomeClientImpl
import miwu.miot.kmp.impl.client.MiotUserClientImpl
import miwu.miot.kmp.impl.provider.MiotLoginProviderImpl
import miwu.miot.kmp.impl.provider.MiotSpecAttrProviderImpl
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import miwu.dispatchers.module as toKoinModule

val MiotApiKoinModule.KMP.Client get() = clientModule
val MiotApiKoinModule.KMP.Provider get() = providerModule

internal val clientModule = module {
    factory<MiotDeviceClientImpl>().bind<MiotDeviceClient>()
    factory<MiotHomeClientImpl>().bind<MiotHomeClient>()
    factory<MiotUserClientImpl>().bind<MiotUserClient>()
}

internal val providerModule = module {
    // Bridge the shared annotation module through the Compiler Plugin-generated extension.
    includes(DispatcherModule().toKoinModule())

    single<MiotLoginProviderImpl>().bind<MiotLoginProvider>()
    single<MiotSpecAttrProviderImpl>().bind<MiotSpecAttrProvider>()
}
