package com.github.miwu.data.miot

import miwu.miot.model.MiotUser
import org.junit.After
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class JvmMiotClientFactoryTest {
    private val factory = JvmMiotClientFactory()

    @After
    fun tearDown() = factory.close()

    @Test
    fun reusesClientsFromTheCurrentUserSession() {
        val user = user("first")

        assertSame(factory.createHomeClient(user), factory.createHomeClient(user))
        assertSame(factory.createUserClient(user), factory.createUserClient(user))
        assertSame(factory.createDeviceClient(user), factory.createDeviceClient(user))
    }

    @Test
    fun replacesTheSessionWhenCredentialsChangeOrFactoryCloses() {
        val first = factory.createHomeClient(user("first"))
        val second = factory.createHomeClient(user("second"))

        assertNotSame(first, second)

        factory.close()
        assertNotSame(second, factory.createHomeClient(user("second")))
    }

    private fun user(id: String) = MiotUser(
        userId = id,
        cUserId = "c-$id",
        nonce = 0,
        ssecurity = "c2VjcmV0",
        psecurity = "",
        passToken = "pass-$id",
        serviceToken = "service-$id",
        deviceId = "device-$id",
    )
}
