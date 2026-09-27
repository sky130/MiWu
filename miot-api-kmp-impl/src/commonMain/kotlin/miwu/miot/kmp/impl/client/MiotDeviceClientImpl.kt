package miwu.miot.kmp.impl.client

import miwu.miot.client.transport.TransportMiotDeviceClient
import miwu.miot.kmp.service.createMiotService
import miwu.miot.kmp.utils.MiotAuthHttpClient
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.SetParams
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotDeviceClientImpl(@InjectedParam user: MiotUser) : TransportMiotDeviceClient() {
    private val httpClient = MiotAuthHttpClient(user)
    private val service = MiotAuthKtorfit(httpClient).createMiotService()

    override suspend fun requestGetProperties(body: GetParams) = service.getDeviceAtt(body)
    override suspend fun requestSetProperties(body: SetParams) = service.setDeviceAtt(body)
    override suspend fun requestAction(body: ActionBody) = service.doAction(body)
    override fun close() = httpClient.close()
}
