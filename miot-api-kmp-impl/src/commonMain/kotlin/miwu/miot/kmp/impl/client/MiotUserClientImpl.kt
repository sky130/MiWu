package miwu.miot.kmp.impl.client

import miwu.miot.client.transport.TransportMiotUserClient
import miwu.miot.kmp.service.createUserService
import miwu.miot.kmp.utils.MiotAuthHttpClient
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.request.GetUserInfo
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotUserClientImpl(
    @InjectedParam private val user: MiotUser,
) : TransportMiotUserClient(user) {
    private val httpClient = MiotAuthHttpClient(user)
    private val service = MiotAuthKtorfit(httpClient).createUserService()

    override suspend fun requestUserInfo(body: GetUserInfo) = service.getUserInfo(body)
    override fun close() = httpClient.close()
}
