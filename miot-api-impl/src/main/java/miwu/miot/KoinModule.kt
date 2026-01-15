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
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.dsl.module

val MiotApiKoinModule.JVM.Client get() = clientModule
val MiotApiKoinModule.JVM.Provider get() = providerModule

internal val clientModule = module {
    factory<MiotDeviceClient> { (user: MiotUser) -> MiotDeviceClientImpl(user) }
    factory<MiotHomeClient> { (user: MiotUser) -> MiotHomeClientImpl(user) }
    factory<MiotUserClient> { (user: MiotUser) -> MiotUserClientImpl(user) }
}

internal val providerModule = module {
    single<MiotLoginProvider> { MiotLoginProviderImpl() }
    single<MiotSpecAttrProvider> { MiotSpecAttrProviderImpl() }
}

