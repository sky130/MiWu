package miwu.miot.impl.client

import miwu.miot.client.transport.TransportMiotUserClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.request.GetUserInfo
import miwu.miot.service.UserService
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.close
import miwu.miot.utils.create
import miwu.miot.utils.miotTimeouts
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotUserClientImpl(
    @InjectedParam private val user: MiotUser,
) : TransportMiotUserClient(user) {
    private val client = OkHttpClient {
        miotTimeouts()
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val service = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(JsonConverterFactory()),
        client = client,
    ).create<UserService>()

    override suspend fun requestUserInfo(body: GetUserInfo) = service.getUserInfo(body)
    override fun close() = client.close()
}
