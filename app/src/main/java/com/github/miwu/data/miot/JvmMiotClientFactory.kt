package com.github.miwu.data.miot

import com.github.miwu.domain.gateway.MiotClientFactory
import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.impl.client.MiotDeviceClientImpl
import miwu.miot.impl.client.MiotHomeClientImpl
import miwu.miot.impl.client.MiotUserClientImpl
import miwu.miot.model.MiotUser

class JvmMiotClientFactory : MiotClientFactory {
    override fun createUserClient(user: MiotUser): MiotUserClient =
        MiotUserClientImpl(user)

    override fun createHomeClient(user: MiotUser): MiotHomeClient =
        MiotHomeClientImpl(user)

    override fun createDeviceClient(user: MiotUser): MiotDeviceClient =
        MiotDeviceClientImpl(user)
}
