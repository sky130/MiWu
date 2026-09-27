package miwu.miot.impl.client

import miwu.miot.client.transport.TransportMiotHomeClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.RunNewScene
import miwu.miot.service.HomeService
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.close
import miwu.miot.utils.create
import miwu.miot.utils.miotTimeouts
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotHomeClientImpl(@InjectedParam user: MiotUser) : TransportMiotHomeClient() {
    private val client = OkHttpClient {
        miotTimeouts()
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val service = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(JsonConverterFactory()),
        client = client,
    ).create<HomeService>()

    override suspend fun requestHomes(body: GetHome) = service.getHomes(body)
    override suspend fun requestDevices(body: GetDevices) = service.getDevices(body)
    override suspend fun requestScenes(body: GetScene) = service.getScenes(body)
    override suspend fun requestRunScene(body: RunNewScene) = service.runScene(body)
    override fun close() = client.close()
}
