package com.github.miwu.data.miot

import com.github.miwu.domain.gateway.MiotClientFactory
import miwu.miot.client.MiotClientSession
import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.impl.client.MiotClientSessionImpl
import miwu.miot.model.MiotUser
import org.koin.core.annotation.Singleton

@Singleton
class JvmMiotClientFactory : MiotClientFactory {
    private var currentUser: MiotUser? = null
    private var currentSession: MiotClientSession? = null

    override fun createUserClient(user: MiotUser): MiotUserClient =
        sessionFor(user).userClient

    override fun createHomeClient(user: MiotUser): MiotHomeClient =
        sessionFor(user).homeClient

    override fun createDeviceClient(user: MiotUser): MiotDeviceClient =
        sessionFor(user).deviceClient

    @Synchronized
    override fun close() {
        currentSession?.close()
        currentSession = null
        currentUser = null
    }

    @Synchronized
    private fun sessionFor(user: MiotUser): MiotClientSession {
        if (currentUser != user || currentSession == null) {
            currentSession?.close()
            currentSession = MiotClientSessionImpl(user)
            currentUser = user
        }
        return checkNotNull(currentSession)
    }
}
