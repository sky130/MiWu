package com.github.miwu.domain.gateway

import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.model.MiotUser

interface MiotClientFactory {
    fun createUserClient(user: MiotUser): MiotUserClient

    fun createHomeClient(user: MiotUser): MiotHomeClient

    fun createDeviceClient(user: MiotUser): MiotDeviceClient
}
