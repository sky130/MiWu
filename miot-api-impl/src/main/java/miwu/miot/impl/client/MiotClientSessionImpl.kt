package miwu.miot.impl.client

import java.util.concurrent.atomic.AtomicBoolean
import miwu.miot.client.MiotClientSession
import miwu.miot.client.transport.TransportMiotDeviceClient
import miwu.miot.client.transport.TransportMiotHomeClient
import miwu.miot.client.transport.TransportMiotUserClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.GetUserInfo
import miwu.miot.model.request.RunNewScene
import miwu.miot.model.request.SetParams
import miwu.miot.service.HomeService
import miwu.miot.service.MiotService
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
class MiotClientSessionImpl(
    @InjectedParam user: MiotUser,
) : MiotClientSession {
    private val closed = AtomicBoolean()
    private val httpClient = OkHttpClient {
        miotTimeouts()
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val retrofit = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(JsonConverterFactory()),
        client = httpClient,
    )
    private val homeService = retrofit.create<HomeService>()
    private val userService = retrofit.create<UserService>()
    private val miotService = retrofit.create<MiotService>()

    override val homeClient = object : TransportMiotHomeClient() {
        override suspend fun requestHomes(body: GetHome) = homeService.getHomes(body)
        override suspend fun requestDevices(body: GetDevices) = homeService.getDevices(body)
        override suspend fun requestScenes(body: GetScene) = homeService.getScenes(body)
        override suspend fun requestRunScene(body: RunNewScene) = homeService.runScene(body)
    }

    override val userClient = object : TransportMiotUserClient(user) {
        override suspend fun requestUserInfo(body: GetUserInfo) = userService.getUserInfo(body)
    }

    override val deviceClient = object : TransportMiotDeviceClient() {
        override suspend fun requestGetProperties(body: GetParams) =
            miotService.getDeviceAtt(body)

        override suspend fun requestSetProperties(body: SetParams) =
            miotService.setDeviceAtt(body)

        override suspend fun requestAction(body: ActionBody) = miotService.doAction(body)
    }

    override fun close() {
        if (closed.compareAndSet(false, true)) httpClient.close()
    }
}
