package miwu.miot.kmp

import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.impl.client.MiotDeviceClientImpl
import miwu.miot.kmp.impl.client.MiotHomeClientImpl
import miwu.miot.kmp.impl.client.MiotUserClientImpl
import miwu.miot.kmp.impl.provider.MiotLoginProviderImpl
import miwu.miot.kmp.impl.provider.MiotSpecAttrProviderImpl
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.dsl.module

val MiotApiKoinModule.KMP.Client get() = clientModule
val MiotApiKoinModule.KMP.Provider get() = providerModule

internal val clientModule = module {
    factory<MiotDeviceClient> { (user: MiotUser) -> MiotDeviceClientImpl(user) }
    factory<MiotHomeClient> { (user: MiotUser) -> MiotHomeClientImpl(user) }
    factory<MiotUserClient> { (user: MiotUser) -> MiotUserClientImpl(user) }
}

internal val providerModule = module {
    single<MiotLoginProvider> { MiotLoginProviderImpl() }
    single<MiotSpecAttrProvider> { MiotSpecAttrProviderImpl() }
}

