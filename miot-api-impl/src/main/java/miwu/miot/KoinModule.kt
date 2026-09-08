package miwu.miot

import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.impl.client.MiotDeviceClientImpl
import miwu.miot.impl.client.MiotHomeClientImpl
import miwu.miot.impl.client.MiotUserClientImpl
import miwu.miot.impl.provider.MiotLoginProviderImpl
import miwu.miot.impl.provider.MiotSpecAttrProviderImpl
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

val MiotApiKoinModule.JVM.Client get() = clientModule
val MiotApiKoinModule.JVM.Provider get() = providerModule

internal val clientModule = module {
    factory<MiotDeviceClientImpl>().bind<MiotDeviceClient>()
    factory<MiotHomeClientImpl>().bind<MiotHomeClient>()
    factory<MiotUserClientImpl>().bind<MiotUserClient>()
}

internal val providerModule = module {
    single<MiotLoginProviderImpl>().bind<MiotLoginProvider>()
    single<MiotSpecAttrProviderImpl>().bind<MiotSpecAttrProvider>()
}

