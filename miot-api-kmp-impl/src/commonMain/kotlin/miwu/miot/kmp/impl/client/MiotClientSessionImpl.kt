package miwu.miot.kmp.impl.client

import miwu.miot.client.MiotClientSession
import miwu.miot.client.transport.TransportMiotDeviceClient
import miwu.miot.client.transport.TransportMiotHomeClient
import miwu.miot.client.transport.TransportMiotUserClient
import miwu.miot.kmp.service.createHomeService
import miwu.miot.kmp.service.createMiotService
import miwu.miot.kmp.service.createUserService
import miwu.miot.kmp.utils.MiotAuthHttpClient
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.GetUserInfo
import miwu.miot.model.request.RunNewScene
import miwu.miot.model.request.SetParams
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotClientSessionImpl(
    @InjectedParam user: MiotUser,
) : MiotClientSession {
    private val httpClient = MiotAuthHttpClient(user)
    private val ktorfit = MiotAuthKtorfit(httpClient)
    private val homeService = ktorfit.createHomeService()
    private val userService = ktorfit.createUserService()
    private val miotService = ktorfit.createMiotService()

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

    override fun close() = httpClient.close()
}
