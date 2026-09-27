package miwu.miot.impl.client

import miwu.miot.client.transport.TransportMiotDeviceClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.SetParams
import miwu.miot.service.MiotService
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.close
import miwu.miot.utils.create
import miwu.miot.utils.miotTimeouts
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotDeviceClientImpl(@InjectedParam user: MiotUser) : TransportMiotDeviceClient() {
    private val client = OkHttpClient {
        miotTimeouts()
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val service = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(JsonConverterFactory()),
        client = client,
    ).create<MiotService>()

    override suspend fun requestGetProperties(body: GetParams) = service.getDeviceAtt(body)
    override suspend fun requestSetProperties(body: SetParams) = service.setDeviceAtt(body)
    override suspend fun requestAction(body: ActionBody) = service.doAction(body)
    override fun close() = client.close()
}
