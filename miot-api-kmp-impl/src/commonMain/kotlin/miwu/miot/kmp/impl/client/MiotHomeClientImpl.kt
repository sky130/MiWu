package miwu.miot.kmp.impl.client

import miwu.miot.client.transport.TransportMiotHomeClient
import miwu.miot.kmp.service.createHomeService
import miwu.miot.kmp.utils.MiotAuthHttpClient
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.RunNewScene
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotHomeClientImpl(@InjectedParam user: MiotUser) : TransportMiotHomeClient() {
    private val httpClient = MiotAuthHttpClient(user)
    private val service = MiotAuthKtorfit(httpClient).createHomeService()

    override suspend fun requestHomes(body: GetHome) = service.getHomes(body)
    override suspend fun requestDevices(body: GetDevices) = service.getDevices(body)
    override suspend fun requestScenes(body: GetScene) = service.getScenes(body)
    override suspend fun requestRunScene(body: RunNewScene) = service.runScene(body)
    override fun close() = httpClient.close()
}
